package nz.govt.natlib.ndha;


import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedConnection;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSource {

    private static Map<String, WarcResource> warcs;

//    @Autowired
    private MemcachedClient memcacheConn;

    public StoreSource(MemcachedClient memcacheConn){
        System.out.println("Initializing Store Source");
        warcs = new HashMap<String, WarcResource>();
        WarcResource newWarc = new WarcResource("WEB-20160603014432482-00000-9193-ubuntu-8443.warc",
                "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");
//        warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", newWarc);
        this.memcacheConn = memcacheConn;
    }


    public boolean warcExists(String name){
        if(name != null && warcs.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name){

//        MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
        CASValue<Object> casVal = memcacheConn.getAndTouch(name, 21600);
        if(casVal != null){
            String value = (String) casVal.getValue();
            return value;
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
//        MemcachedClient memcacheConn = MemcachedClientFactory.getNewConnection();
//        if(warcExists(name)){
//            // Update timestamp
//            warcs.get(name).regenLastUpdated();
//            return true;
//        }
//        else{
//            WarcResource newWarc = new WarcResource(name, "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name);
            OperationFuture<Boolean> op = memcacheConn.set(name, 21600, "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name);
//            warcs.put(name, newWarc);

//        try {
//            memcacheConn.getConnection().shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            return true;
//        }
    }

}
