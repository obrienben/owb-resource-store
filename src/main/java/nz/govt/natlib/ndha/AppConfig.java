package nz.govt.natlib.ndha;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Developer on 20/09/2016.
 */
@Configuration
public class AppConfig {

    @Bean
    public StoreSource storeSource(){
        return new StoreSource(memcache());
    }

    @Bean
    public MemcachedClient memcache(){
        MemcachedClient mem = null;
        try {
            mem = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses("192.168.127.135:11211"));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Log error that can't get connection
            return null;
        }
        return mem;
    }

}
