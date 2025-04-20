package com.example.oilstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan("com.example.oilstorage.entity") // 指定实体类所在包
@EnableScheduling
public class OilStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(OilStorageApplication.class, args);
    }

}
