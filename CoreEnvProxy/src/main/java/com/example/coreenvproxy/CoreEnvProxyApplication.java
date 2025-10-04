package com.example.coreenvproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CoreEnvProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreEnvProxyApplication.class, args);
    }
}
