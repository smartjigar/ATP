package org.catenax.atp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "org.catenax.atp")
@ConfigurationPropertiesScan
@EnableCaching
public class AtpMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtpMainApplication.class, args);
    }

}
