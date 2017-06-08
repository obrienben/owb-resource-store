package nz.govt.natlib.ndha;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 * Created by Developer on 20/09/2016.
 */
@Configuration
public class AppConfig {

    private static final Logger log = LogManager.getLogger(AppConfig.class);

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
        log.info("Configuring Store Source");
//        MemcachedClientFactory.setConnectionDetails(memcacheHost, memcachePort);
        Resource dataResource = resourceLoader.getResource("classpath:path-index.txt");
        Resource configResource = resourceLoader.getResource("classpath:resourcestore_ehcache.xml");
        return new StoreSourceEhcacheImpl(preloadData, storeLocation, dataResource, configResource);
//        return new StoreSourceMemcacheImpl(preloadData, storeLocation, dataResource);
//        return new StoreSourceSQLiteImpl(preloadData, storeLocation, dataResource);
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
