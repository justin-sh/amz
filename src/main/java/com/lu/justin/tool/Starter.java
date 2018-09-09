package com.lu.justin.tool;

import com.lu.justin.tool.file.service.StorageProperties;
import com.lu.justin.tool.file.service.StorageService;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.MultipartConfigElement;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableScheduling
public class Starter extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Starter.class);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return args -> {
            log.info("Running default command line with: " + Arrays.asList(args));
            storageService.deleteAll();
            storageService.init();
        };
    }

    @Bean
    TomcatServletWebServerFactory containerFactory(MultipartConfigElement mpe) {
        return new TomcatServletWebServerFactory() {
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
                    ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize((int) mpe.getMaxFileSize() * 10);
                    log.info("set tomcat max swallow size:" + mpe.getMaxFileSize() * 10);
                }
            }
        };
    }

    @Bean
    CloseableHttpClient httpClient() {
        return HttpClientBuilder.create()
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout((int) TimeUnit.SECONDS.toMillis(10))
                        .build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout((int) TimeUnit.SECONDS.toMillis(3))
                        .build())
                .build();
    }

}
