package com.lu.justin.tool;

import com.lu.justin.tool.file.service.StorageProperties;
import com.lu.justin.tool.file.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Starter {

    private static final Logger log = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return args -> {
            log.info("Running default command line with: " + Arrays.asList(args));
            storageService.deleteAll();
            storageService.init();
        };
    }
}
