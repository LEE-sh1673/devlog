package com.devlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevlogApplication {

    public static void main(String[] args) {
        System.out.println("DevlogApplication.main");
        SpringApplication.run(DevlogApplication.class, args);
    }

}
