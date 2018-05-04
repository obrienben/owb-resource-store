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

    @Value("${resourcestore.resourceStorePool}")
    public String resourceStorePool;

    @Bean
    public StoreSource storeSource(){
        log.info("Configuring Store Source");
        Resource dataResource = resourceLoader.getResource("classpath:path-index.txt");
        Resource configResource = resourceLoader.getResource("classpath:resourcestore_ehcache.xml");
        return new StoreSourceEhcacheImpl(preloadData, storeLocation, dataResource, configResource, resourceStorePool);
    }

}
