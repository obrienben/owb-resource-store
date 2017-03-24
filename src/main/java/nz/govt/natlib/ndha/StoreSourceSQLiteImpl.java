package nz.govt.natlib.ndha;


//import net.openhft.chronicle.map.ChronicleMap;
//import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.OperationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSourceSQLiteImpl implements StoreSource{

    private static final Logger log = LogManager.getLogger(StoreSourceSQLiteImpl.class);
    private static Map<String, WarcResource> warcs;
    private static String preLoadData;
    private static String storeLocation = "remote";
    private static Resource dataFile;
//    private static MemcachedClient memcacheConn;
    private static Connection sqliteConn = null;

    public StoreSourceSQLiteImpl(String preloadData, String storeLocation, Resource dataFile){
        this.preLoadData = preloadData;
        this.storeLocation = storeLocation;
        this.dataFile = dataFile;
        System.out.println("Initializing Store Source");
        warcs = new HashMap<String, WarcResource>();
        WarcResource newWarc = new WarcResource("WEB-20160603014432482-00000-9193-ubuntu-8443.warc",
                "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");
//        warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", newWarc);
//        this.memcacheConn = memcacheConn;
        if(this.preLoadData.equals("true")){
            System.out.println("Pre-loading store with static data.");
            preloadData();
        }

        // Disable spymemcached logging
//        Properties systemProperties = System.getProperties();
//        systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
//        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger");
//        java.util.logging.Logger.getLogger("net.spy.memcached").setLevel(Level.OFF);
//        System.setProperties(systemProperties);

//        try {
//            sqliteConn = DriverManager.getConnection("jdbc:sqlite:C:/AppDev/owresourcestore.db");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


    }


    public boolean warcExists(String name){
        if(name != null && warcs.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name){

        if(storeLocation.equals("remote")){

            if(!isConnectionAlive()){
                return null;
            }

            try {
                Statement statement = sqliteConn.createStatement();
                statement.setQueryTimeout(30);
                ResultSet rs = statement.executeQuery("SELECT * FROM filepath WHERE name = '" + name + "'");
                while(rs.next())
                {
                    // read the result set
                    System.out.println("name = " + rs.getString("name"));
                    System.out.println("path = " + rs.getString("path"));
                    System.out.println("date_created = " + rs.getString("date_created"));

                    String path = rs.getString("path");
                    return path;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }


        }
        else if(storeLocation.equals("local")){
            if(warcExists(name)){
               WarcResource warc = warcs.get(name);
                return warc.getFilepath();
            }
        }


//        if(warcExists(name)){
//            return warcs.get(name).getFilepath();
//        }
        return null;
    }

    public boolean addWarc(String name, String path){
//        MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
//        if(!isConnectionAlive()){
//            return false;
////            startConnection();
//        }
        String filePath = path;
//        if(warcExists(name)){
//            // Update timestamp
//            warcs.get(name).regenLastUpdated();
//            return true;
//        }
//        else{
//            WarcResource newWarc = new WarcResource(name, "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name);
        if(name.equals("WEB-20160603014432482-00000-9193-ubuntu-8443.warc")){
            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc")){
            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc")){
            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc")){
            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc")){
            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }


        try {

            Long insertDateTime = Instant.now().toEpochMilli();

            Statement statement = sqliteConn.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("insert into filepath values('" + name + "', '" + path + "', '" + insertDateTime.toString() + "')");

            System.out.println("INSERTED name = " + name + ". path = " + path + ". date_created = " + insertDateTime.toString());
            //TODO detect if key already exists, then update datetime

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void forceEviction(){

        if(!isConnectionAlive()){
            return;
        }

        try {
            // Calculate eviction cut off time
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Instant.now().toEpochMilli());
            cal.add(Calendar.HOUR_OF_DAY, -6);
            long evictionCutOff = cal.getTimeInMillis();

            Statement statement = sqliteConn.createStatement();
            statement.setQueryTimeout(30);
            //TODO change SELECT to DELETE
            ResultSet rs = statement.executeQuery("SELECT * FROM filepath WHERE date_created < '" + evictionCutOff + "'");
            while(rs.next()){
                System.out.println("name = " + rs.getString("name") + ". path = " + rs.getString("path") + ". date_created = " + rs.getString("date_created"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }

    private void tableExists(){
        if(!isConnectionAlive()){
            return;
        }

        try {
            Statement statement = sqliteConn.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM sqlite_master WHERE name = 'filepath' AND type='table'");

            while(rs.next()){
                if(rs.getString("name").equals("filepath")){
                    return;
                }
            }

            // If table does not exist then create
//            statement.executeUpdate("drop table if exists filepath");
            statement.executeUpdate("create table filepath (name TEXT NOT NULL PRIMARY KEY, path TEXT, date_created REAL)");

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    private void preloadData() {
        log.debug("Resource Store initialization: preloading data");
        log.debug("Resource Store storeLocation: " + storeLocation);
        log.debug("Resource Store dataFile: " + dataFile.toString());

        try {
            InputStream is = dataFile.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while((line = br.readLine()) != null){
                String[] tokens = line.split(" ");
                WarcResource newWarc = new WarcResource(tokens[0], tokens[1]);
                warcs.put(tokens[0], newWarc);
            }

//            for(String name : warcs.keySet()){
//                log.debug("Reading warc from file: " + name);
//            }

        } catch (IOException e) {
            log.error("Unable to read preload data.", e);
        }

        // Push pre-loaded data to memcache instance
        if(storeLocation.equals("remote")){
            try {
                MemcachedClient conn = MemcachedClientFactory.getNewConnection();

                for(String name : warcs.keySet()){
                    log.debug("Preloading resource into memcache server: " + name);
                    OperationFuture<Boolean> op = conn.set(name, 21600, warcs.get(name).getFilepath());

                }

                // Closing the connection seems to break the set functionality
//                conn.getConnection().shutdown();
            } catch (Exception e) {
                log.error("Unable to preload memcache server.", e);
            }
        }

    }

    public void startConnection(String method) throws SQLException {
        sqliteConn = DriverManager.getConnection("jdbc:sqlite:C:/AppDev/owresourcestore.db");

        if(method.equals("add")){
            tableExists();
            forceEviction();
        }
    }

    public void endConnection() throws SQLException {
        sqliteConn.close();
    }

    public boolean isConnectionAlive(){
        if(sqliteConn == null){
            return false;
        }

        try {
            if(sqliteConn.isClosed()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static void main(String[] args) {
//        Resource dataResource = null;
        StoreSource store = new StoreSourceSQLiteImpl("false", "remote", null);

        try {
            store.startConnection("add");

//            store.addWarc("V1-FL18177611.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177611.warc");
//            store.addWarc("V1-FL18177614.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177614.warc");
//            store.addWarc("V1-FL18177615.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177615.warc");
//            store.addWarc("V1-FL18177616.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177616.warc");
//            store.addWarc("V1-FL18177617.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177617.warc");

            store.addWarc("V1-FL1113.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1113.warc");
            store.addWarc("V1-FL1114.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1114.warc");
            store.addWarc("V1-FL1115.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1115.warc");
            store.addWarc("V1-FL1116.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1116.warc");
            store.addWarc("V1-FL1117.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1117.warc");

//            store.getWarc("V1-FL18177613.warc");
//            store.forceEviction();

            store.endConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
