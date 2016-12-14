package nz.govt.natlib.ndha;


import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedConnection;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSource {

    private static Map<String, WarcResource> warcs;
    private static String preLoadData;
    private static String storeLocation = "remote";
    private static Resource dataFile;
//    @Autowired
//    private MemcachedClient memcacheConn;

    public StoreSource(String preloadData, String storeLocation, Resource dataFile){
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
    }


    public boolean warcExists(String name){
        if(name != null && warcs.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name){

        if(storeLocation.equals("remote")){
            MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
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

    public boolean addWarc(String name){
        MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
        String path = "";
//        if(warcExists(name)){
//            // Update timestamp
//            warcs.get(name).regenLastUpdated();
//            return true;
//        }
//        else{
//            WarcResource newWarc = new WarcResource(name, "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name);
        if(name.equals("WEB-20160603014432482-00000-9193-ubuntu-8443.warc")){
            path = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc")){
            path = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc")){
            path = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc")){
            path = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }
        else if(name.equals("NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc")){
            path = "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\" + name;
        }

        OperationFuture<Boolean> op = memcacheConn.set(name, 21600, path);
//            warcs.put(name, newWarc);

//        try {
//            memcacheConn.getConnection().shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            return true;
//        }
    }

    private void preloadData() {
        System.out.println("storeLocation: " + storeLocation);
        System.out.println("dataFile: " + dataFile.toString());
        try {
            InputStream is = dataFile.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while((line = br.readLine()) != null){
//                System.out.println("ResourceStore line: " + line);
                String[] tokens = line.split(" ");
                WarcResource newWarc = new WarcResource(tokens[0], tokens[1]);
                warcs.put(tokens[0], newWarc);
            }

            for(String name : warcs.keySet()){
                System.out.println("ResourceStore warc: " + name);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }



//        warcs.put();
    }

}
