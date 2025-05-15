package com.booktable.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTableApplication.class, args);
    }
}