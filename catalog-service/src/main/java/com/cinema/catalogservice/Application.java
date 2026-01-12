package com.cinema.catalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
// Trigger redeploy to recover stalled service
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
