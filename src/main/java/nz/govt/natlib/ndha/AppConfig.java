package nz.govt.natlib.ndha;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * Created by Developer on 20/09/2016.
 */
@Configuration
public class AppConfig {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${resourcestore.preloadData}")
    public String preloadData;

    @Value("${resourcestore.storeLocation}")
    public String storeLocation;

    @Value("${resourcestore.memcacheHost}")
    public String memcacheHost;

    @Value("${resourcestore.memcachePort}")
    public String memcachePort;

    @Bean
    public StoreSource storeSource(){
        MemcachedClientFactory.setConnectionDetails(memcacheHost, memcachePort);
        Resource dataResource = resourceLoader.getResource("classpath:path-index.txt");
        return new StoreSource(preloadData, storeLocation, dataResource);
    }

//    @Bean
//    public MemcachedClient memcache(){
//        MemcachedClient mem = null;
//        try {
//            mem = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses("192.168.127.135:11211"));
//        } catch (IOException e) {
//            e.printStackTrace();
//            // TODO Log error that can't get connection
//            return null;
//        }
//        return mem;
//    }

}
