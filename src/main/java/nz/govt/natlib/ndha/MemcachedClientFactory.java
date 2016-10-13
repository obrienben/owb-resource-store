package nz.govt.natlib.ndha;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;


public class MemcachedClientFactory {

    private static String HOST_ADDRESS = "192.168.127.135";
    private static String HOST_PORT = "11211";
    private static String HOST_SEPARATOR = ":";

    public static MemcachedClient getNewConnection() {
        MemcachedClient mem = null;
        try {
            mem = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(HOST_ADDRESS+HOST_SEPARATOR+HOST_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Log error that can't get connection
            return null;
        }
        return mem;
    }
}
