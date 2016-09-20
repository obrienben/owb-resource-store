package nz.govt.natlib.ndha;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Developer on 20/09/2016.
 */
@Configuration
public class AppConfig {

    @Bean
    public StoreSource storeSource(){
        return new StoreSource();
    }
}
