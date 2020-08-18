package com.honglin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BlogServerApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogServerApp.class, args);
    }
}
