package nz.govt.natlib.ndha;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class MemcachedClientFactory {

    private static final Logger log = LogManager.getLogger(MemcachedClientFactory.class);
    private static String HOST_ADDRESS = null;
    private static String HOST_PORT = null;
    private static String HOST_SEPARATOR = ":";

    public static void setConnectionDetails(String host, String port){
        HOST_ADDRESS = host;
        HOST_PORT = port;
        System.out.println("HOST_ADDRESS set: " + HOST_ADDRESS);
        System.out.println("HOST_PORT set: " + HOST_PORT);
    }

    public static MemcachedClient getNewConnection() {
        MemcachedClient mem = null;
        if(HOST_ADDRESS == null || HOST_PORT == null){
            log.error("Failed to establish Memcache connection - host and/or port not set.");
            return null;
        }

        try {
            mem = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(HOST_ADDRESS+HOST_SEPARATOR+HOST_PORT));
        } catch (IOException e) {
            log.error("Unable to establish new connection to Memcache server.", e);
            return null;
        }
        return mem;
    }
}
