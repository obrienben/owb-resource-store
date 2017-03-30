package nz.govt.natlib.ndha;


//import net.openhft.chronicle.map.ChronicleMap;
//import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
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

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSourceSQLiteImpl implements StoreSource{

    private static final Logger log = LogManager.getLogger(StoreSourceSQLiteImpl.class);
    private static Map<String, WarcResource> warcs;
    private static String preLoadData;
    private static String storeLocation = "remote";
    private static Resource dataFile;
    private Connection sqliteConn = null;

    public StoreSourceSQLiteImpl(String preloadData, String storeLocation, Resource dataFile){
        this.preLoadData = preloadData;
        this.storeLocation = storeLocation;
        this.dataFile = dataFile;
        System.out.println("Initializing Store Source");
//        warcs = new HashMap<String, WarcResource>();
//        WarcResource newWarc = new WarcResource("WEB-20160603014432482-00000-9193-ubuntu-8443.warc",
//                "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");
//        warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", newWarc);
        if(this.preLoadData.equals("true")){
            System.out.println("Pre-loading store with static data.");
            preloadData();
        }

    }


    public boolean warcExists(String name){
        if(name != null && warcs.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name) throws SQLException {

        if(storeLocation.equals("remote")){

            if(!isConnectionAlive()){
                throw new SQLException("Store connection is not alive");
            }

            int retry_cnt = 0;
            Statement statement = null;

            while(retry_cnt < 3) {
                try {
                    statement = sqliteConn.createStatement();

                    statement.setQueryTimeout(30);

                    ResultSet rs = statement.executeQuery("SELECT * FROM filepath WHERE name = '" + name + "'");
                    while (rs.next()) {
                        // read the result set
//                        throw new SQLException("ta da");
//                    System.out.println("name = " + rs.getString("name") + ". path = " + rs.getString("path") + ". date_created = " + rs.getString("date_created"));
                        String path = rs.getString("path");
                        return path;
                    }
                }
                catch(SQLException ex){
                    // If query exceeded max retries
                    if(retry_cnt >= 2){
                        System.out.println(Thread.currentThread().getName() + "[READ_FAILED] retry count: " + retry_cnt);
//                        endConnection();
                        throw new SQLException(ex);
                    }
                    try{
                        endConnection();
                        Thread.sleep(50);
                        sqliteConn.clearWarnings();
                        startConnection(false);
                    }
                    catch(Exception e1){
                        e1.printStackTrace();
                        return null;
                    }

                }
                // Retry query a second time
                retry_cnt++;
            }

        }
        else if(storeLocation.equals("local")){
            if(warcExists(name)){
               WarcResource warc = warcs.get(name);
                return warc.getFilepath();
            }
        }

        return null;
    }

    public boolean addWarc(String name, String path) throws SQLException {
        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }

//        String filePath = path;
//        if(name.equals("WEB-20160603014432482-00000-9193-ubuntu-8443.warc")){
//            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name;
//        }
//        else if(name.equals("NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc")){
//            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
//        }
//        else if(name.equals("NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc")){
//            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
//        }
//        else if(name.equals("NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc")){
//            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
//        }
//        else if(name.equals("NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc")){
//            filePath = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
//        }

        int retry_cnt = 0;
        Long insertDateTime = Instant.now().toEpochMilli();
        Statement statement = null;

        while(retry_cnt < 20){

            try {
                statement = sqliteConn.createStatement();
                statement.setQueryTimeout(30);
                statement.executeUpdate("insert into filepath values('" + name + "', '" + path + "', '" + insertDateTime.toString() + "')");
                break;
//            System.out.println("INSERTED name = " + name + ". path = " + path + ". date_created = " + insertDateTime.toString());

            } catch (SQLException e) {
                // If the filepath already exists, then update row with current timestamp
                if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed: filepath.name")) {
//                    try {
                        //TODO Will this hog the db on big harvests, is it better to only update if the timestamp is
                        statement.executeUpdate("UPDATE filepath SET date_created = " + insertDateTime.toString() + " WHERE name = '" + name + "'");
//                    System.out.println("UPDATED name = " + name + ". path = " + path + ". date_created = " + insertDateTime.toString());
                        break;
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                        return false;
//                    }
                }

                if (e.getMessage().contains("[SQLITE_BUSY]  The database file is locked")) {
                    try {
                        endConnection();
                        if(retry_cnt < 10) Thread.sleep(50);
                        else Thread.sleep(1000);
                        startConnection(false);
                        retry_cnt++;
                        System.out.println(Thread.currentThread().getName() + "[SQLITE_BUSY] retry count: " + retry_cnt);
                        continue;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        return false;
                    }
                }

                // Some other error happened
                return false;
            }
        }
        return true;
    }

    /*
        Delete entries in Resource Store that are older than specified threshold.
     */
    private void forceEviction() throws SQLException {

        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }

        // Calculate eviction cut off time
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Instant.now().toEpochMilli());
        cal.add(Calendar.HOUR_OF_DAY, -4);
//        cal.add(Calendar.MINUTE, -3);
        long evictionCutOff = cal.getTimeInMillis();

        Statement statement = sqliteConn.createStatement();
        statement.setQueryTimeout(30);
//            ResultSet rs = statement.executeQuery("SELECT * FROM filepath WHERE date_created < '" + evictionCutOff + "'");
//            while(rs.next()){
//                System.out.println("EVICTING ROW: name = " + rs.getString("name") + ". path = " + rs.getString("path") + ". date_created = " + rs.getString("date_created"));
//            }
        statement.executeUpdate("DELETE FROM filepath WHERE date_created < '" + evictionCutOff + "'");


//TODO        Also evict from prefetch table
    }

    /*
        Check if database has filepath table, if not then create it.
        Only needed when starting for first time with an empty database.
     */
    private void createTableIfNotExists() throws SQLException {
        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }
        boolean addFilePathTable = true;
        boolean addHashIndexTable = true;

        Statement statement = sqliteConn.createStatement();
        statement.setQueryTimeout(30);
        ResultSet rs = null;
        rs = statement.executeQuery("SELECT * FROM sqlite_master WHERE name = 'filepath' AND type='table'");
        while(rs.next()){
            if(rs.getString("name").equals("filepath")){
                addFilePathTable = false;
            }
        }

        rs = statement.executeQuery("SELECT * FROM sqlite_master WHERE name = 'hashindex' AND type='table'");
        while(rs.next()){
            if(rs.getString("name").equals("hashindex")){
                addHashIndexTable = false;
            }
        }

        // If table does not exist then create
//            statement.executeUpdate("drop table if exists filepath");
        if(addFilePathTable)
            statement.executeUpdate("create table filepath (name TEXT NOT NULL PRIMARY KEY, path TEXT, date_created REAL)");
        if(addHashIndexTable)
            statement.executeUpdate("create table hashindex (hash TEXT NOT NULL PRIMARY KEY, date_created REAL)");
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

    public void startConnection(boolean flag) throws Exception {
        Class.forName("org.sqlite.JDBC");
        sqliteConn = DriverManager.getConnection("jdbc:sqlite:C:/AppDev/owresourcestore.db");
        if(flag){
            createTableIfNotExists();
            forceEviction();
        }
    }

    public void endConnection() throws SQLException {
        sqliteConn.close();
    }

    public boolean isConnectionAlive() throws SQLException {
        if(sqliteConn == null){
            return false;
        }

        if(sqliteConn.isClosed()){
            return false;
        }

        return true;
    }

    /*****************************
        HashIndex table functions
     */
    public boolean supportsHashChecking() {
        return true;
    }

    public boolean hashIndexExists(String value) throws SQLException {
        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }

        Statement statement = sqliteConn.createStatement();
        statement.setQueryTimeout(30);
        ResultSet rs = statement.executeQuery("SELECT * FROM hashindex WHERE hash = '" + value + "'");
        while(rs.next()){
            if(rs.getString("hash").equals(value)){
                return true;
            }
        }
        return false;
    }

    public void addHashIndex(String value) throws SQLException {
        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }
        Long insertDateTime = Instant.now().toEpochMilli();
        Statement statement = sqliteConn.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("insert into hashindex values('" + value + "', '" + insertDateTime.toString() + "')");
    }

    public void updateHashIndex(String value) throws SQLException {
        if(!isConnectionAlive()){
            throw new SQLException("Store connection is not alive");
        }
        Long insertDateTime = Instant.now().toEpochMilli();
        Statement statement = sqliteConn.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("UPDATE hashindex SET date_created = " + insertDateTime.toString() + " WHERE hash = '" + value + "'");
    }



    private static void test_add(StoreSource test_store, Map<String, String> map) throws Exception {
        for(Map.Entry<String, String> entry : map.entrySet()){
            Long write_start = Instant.now().toEpochMilli();
            test_store.addWarc(entry.getKey(), entry.getValue());
            Long write_end = Instant.now().toEpochMilli() - write_start;
            System.out.println(Thread.currentThread().getName() + " - Key: " + entry.getKey() + ". Write took: " + write_end.toString() + "ms");
        }
    }

    private static void test_read(StoreSource test_store, Map<String, String> map) throws Exception {
        for(Map.Entry<String, String> entry : map.entrySet()){
            test_store.startConnection(false);
            Long read_start = Instant.now().toEpochMilli();
            String retVal = test_store.getWarc(entry.getKey());
            Long read_end = Instant.now().toEpochMilli() - read_start;
            test_store.endConnection();
            System.out.println(Thread.currentThread().getName() + " - Value: " + retVal + ". Read took: " + read_end.toString() + "ms");
        }
    }


    public static void main(String[] args) {
//        Resource dataResource = null;
//        StoreSource store = new StoreSourceSQLiteImpl("false", "remote", null);
        Map<String, String> test_data_1 = new HashMap<>();
        test_data_1.put("V1-FL18177611.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177611.warc");
        test_data_1.put("V1-FL18177614.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177614.warc");
        test_data_1.put("V1-FL18177615.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177615.warc");
        test_data_1.put("V1-FL18177616.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177616.warc");
        test_data_1.put("V1-FL18177617.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL18177617.warc");

        Map<String, String> test_data_2 = new HashMap<>();
        test_data_2.put("V1-FL1113.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1113.warc");
        test_data_2.put("V1-FL1114.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1114.warc");
        test_data_2.put("V1-FL1115.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1115.warc");
        test_data_2.put("V1-FL1116.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1116.warc");
        test_data_2.put("V1-FL1117.warc", "/exlibris3/permanent_storage/file_02/2016/02/15/file_1/V1-FL1117.warc");


        Runnable task_1 = () -> {
            try {
                StoreSource store = new StoreSourceSQLiteImpl("false", "remote", null);

                test_read(store, test_data_2);
                test_read(store, test_data_1);
                test_read(store, test_data_2);
                test_read(store, test_data_1);

                store.startConnection(true);
                test_add(store, test_data_1);
                store.endConnection();

                test_read(store, test_data_1);

                store.startConnection(true);
                test_add(store, test_data_2);
                store.endConnection();

                test_read(store, test_data_2);
                test_read(store, test_data_1);
                test_read(store, test_data_2);
                test_read(store, test_data_1);

                store.startConnection(true);
                test_add(store, test_data_2);
                store.endConnection();

                store.startConnection(true);
                test_add(store, test_data_1);
                store.endConnection();

                store.startConnection(true);
                test_add(store, test_data_2);
                store.endConnection();

                store.startConnection(true);
                test_add(store, test_data_1);
                store.endConnection();

                test_read(store, test_data_2);
                test_read(store, test_data_1);
                test_read(store, test_data_2);
                test_read(store, test_data_1);

//            store.getWarc("V1-FL18177613.warc");
//            store.forceEviction();
                store.endConnection();

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable task_2 = () -> {
            try {
                StoreSource store = new StoreSourceSQLiteImpl("false", "remote", null);

                store.startConnection(true);
                test_add(store, test_data_2);
                store.endConnection();

                test_read(store, test_data_2);
                test_read(store, test_data_2);

                store.startConnection(true);
                test_add(store, test_data_1);
                store.endConnection();

                store.startConnection(true);
                test_add(store, test_data_2);
                store.endConnection();

                test_read(store, test_data_2);
                test_read(store, test_data_1);
                test_read(store, test_data_2);

                store.startConnection(true);
                test_add(store, test_data_1);
                store.endConnection();

//            store.getWarc("V1-FL18177613.warc");
//            store.forceEviction();
                store.endConnection();

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(task_1);
        Thread thread2 = new Thread(task_2);
        Long write_start = Instant.now().toEpochMilli();
        thread1.start();
        thread2.start();
        while(thread1.isAlive() || thread2.isAlive()){
            //loop
        }
        Long write_end = Instant.now().toEpochMilli() - write_start;
        System.out.println(Thread.currentThread().getName() + " - Total Test Time: " + write_end.toString() + "ms");

    }
}
