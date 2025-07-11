 package com.motive.numberverification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class NumberVerificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NumberVerificationApplication.class, args);
    }
}
