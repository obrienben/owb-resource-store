package nz.govt.natlib.ndha;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSourceEhcacheImpl implements StoreSource{

    private static final Logger log = LogManager.getLogger(StoreSourceEhcacheImpl.class);
    private static Map<String, WarcResource> warcs;
    private static String preLoadData;
    private static String storeLocation = "remote";
    private static String cacheName = "resourceStore";
    private static Resource dataFile;
    private static Resource configFile;
    private static Cache<String, String> storeCache;
    private static CacheManager cacheManager;

    public StoreSourceEhcacheImpl(String preloadData, String storeLocation, Resource dataFile, Resource configFile){
        this.preLoadData = preloadData;
        this.storeLocation = storeLocation;
        this.dataFile = dataFile;
        this.configFile = configFile;
        System.out.println("Initializing Store Source");
        warcs = new HashMap<String, WarcResource>();
        WarcResource newWarc = new WarcResource("WEB-20160603014432482-00000-9193-ubuntu-8443.warc",
                "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");

        try {
            initialiseCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", newWarc);
//        this.memcacheConn = memcacheConn;
        if(this.preLoadData.equals("true")){
            System.out.println("Pre-loading store with static data.");
            try {
//                startConnection(false);
                preloadData();
//                endConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initialiseCache() throws IOException {
//        URL configUrl = getClass().getResource("/resourcestore_ehcache.xml");
//        Resource dataResource = resourceLoader.getResource("classpath:path-index.txt");
        Configuration xmlConfig = new XmlConfiguration(configFile.getURL());
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
//        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(cacheName,
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
//                .build();
        cacheManager.init();
        storeCache = cacheManager.getCache(cacheName, String.class, String.class);
    }

    public boolean warcExists(String name){
        if(name != null && storeCache.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name){

        if(storeLocation.equals("remote")){

            if(storeCache != null){
                String value = storeCache.get(name);
                if(value != null) {
                    return value;
                }
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

    public boolean addWarc(String name, String path){

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

        if(storeCache != null){
            if(name != null && !name.isEmpty() && path != null && !path.isEmpty()){
                storeCache.put(name, path);
                String value = storeCache.get(name);
                if(path.equals(value)){
                    return true;
                }
            }
        }

        return false;
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

        // Push pre-loaded data to ehcache
        if(storeLocation.equals("remote")){
            try {
                for(String name : warcs.keySet()){
                    log.debug("Preloading resource into Ehcahce server: " + name);
                    addWarc(name, warcs.get(name).getFilepath());
                }
            } catch (Exception e) {
                log.error("Unable to preload Ehcahce.", e);
            }
        }

    }

    // Placeholder
    public void startConnection(boolean flag) throws Exception{}

    // Placeholder
    public void endConnection()throws Exception {}

    public boolean isConnectionAlive(){
        if(storeCache == null){
            return false;
        }
        return true;
    }

}
