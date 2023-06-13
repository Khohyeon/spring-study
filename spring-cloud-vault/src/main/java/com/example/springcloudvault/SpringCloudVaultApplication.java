package com.example.springcloudvault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyConfiguration.class)
public class SpringCloudVaultApplication implements CommandLineRunner {

    private final MyConfiguration myConfiguration;

    public SpringCloudVaultApplication(MyConfiguration myConfiguration) {
        this.myConfiguration = myConfiguration;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudVaultApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(SpringCloudVaultApplication.class);

        logger.info("----------------------------------------");
        logger.info("Configuration properties");
        logger.info("   example.username is {}", myConfiguration.getUsername());
        logger.info("   example.password is {}", myConfiguration.getPassword());
        logger.info("----------------------------------------");
    }
}
