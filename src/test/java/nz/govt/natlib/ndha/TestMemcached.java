
import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;

public class TestMemcached {

//    private static String HOST_ADDRESS = "192.168.127.135";
    private static String HOST_ADDRESS = "appserv16-z1.natlib.govt.nz";
    private static String HOST_PORT = "11211";
    private static String HOST_SEPARATOR = ":";

    public static MemcachedClient getNewConnection() {
        MemcachedClient mem = null;
        try {
            System.out.println("Trying to establish connection to memcache at " + HOST_ADDRESS + HOST_SEPARATOR + HOST_PORT);
            mem = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(HOST_ADDRESS + HOST_SEPARATOR + HOST_PORT));
        } catch (IOException e) {
            System.out.println("FAILED: could not establish connection to memcache.");
            e.printStackTrace();
            // TODO Log error that can't get connection
            return null;
        }
        return mem;
    }

    public static void main(String[] args) {

        MemcachedClient mem = getNewConnection();

        mem.set("a-test-key", 21600, "a-test-value");


        CASValue<Object> casVal = mem.getAndTouch("a-test-key", 21600);
        if(casVal != null){
            String value = (String) casVal.getValue();
            System.out.println("SUCCESS: returned value from memcache - " + value);
        }
        else{
            System.out.println("FAILED: no value returned from memcache.");
        }
    }


}
