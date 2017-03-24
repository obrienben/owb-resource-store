package nz.govt.natlib.ndha;


import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.OperationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSourceMemcacheImpl implements StoreSource{

    private static final Logger log = LogManager.getLogger(StoreSourceMemcacheImpl.class);
    private static Map<String, WarcResource> warcs;
    private static String preLoadData;
    private static String storeLocation = "remote";
    private static Resource dataFile;
    private static MemcachedClient memcacheConn;

    public StoreSourceMemcacheImpl(String preloadData, String storeLocation, Resource dataFile){
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
        Properties systemProperties = System.getProperties();
//        systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger");
        java.util.logging.Logger.getLogger("net.spy.memcached").setLevel(Level.OFF);
        System.setProperties(systemProperties);
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

            CASValue<Object> casVal = memcacheConn.getAndTouch(name, 21600);
            if(casVal != null){
                String value = (String) casVal.getValue();
                return value;
            }
        }
        else if(storeLocation.equals("local")){
            if(warcExists(name)){
               WarcResource warc = warcs.get(name);
                return warc.getFilepath();
            }
        }

//        try {
//            memcacheConn.getConnection().shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        if(warcExists(name)){
//            return warcs.get(name).getFilepath();
//        }
        return null;
    }

    public boolean addWarc(String name, String path){
//        MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
        if(!isConnectionAlive()){
            return false;
//            startConnection();
        }
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

        OperationFuture<Boolean> op = memcacheConn.set(name, 21600, filePath);
        OperationStatus os = op.getStatus();
//        memcacheConn.shutdown(1)
        return os.isSuccess();
//            warcs.put(name, newWarc);

//        try {
//            memcacheConn.getConnection().shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//            return true;
//        }
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

    public void startConnection(String method) throws Exception{
        memcacheConn = MemcachedClientFactory.getNewConnection();
    }

    public void endConnection()throws Exception {
        memcacheConn.shutdown(1, TimeUnit.SECONDS);
    }

    public boolean isConnectionAlive(){
        if(memcacheConn == null){
            return false;
        }
        return memcacheConn.getConnection().isAlive();
    }
}
