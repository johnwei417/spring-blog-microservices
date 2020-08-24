package com.honglin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
//@EnableEurekaClient
@EnableDiscoveryClient
public class EmailApp {
    public static void main(String[] args) {
        SpringApplication.run(EmailApp.class, args);
    }
}
