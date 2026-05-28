package com.energy.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.energy.practice",
    "day1.microservices",
    "day2.messaging",
    "day3.database",
    "day4.javaspring",
    "day5.tdd",
    "day6.scaling"
})
@EntityScan(basePackages = {"day3.database"})
@EnableJpaRepositories(basePackages = {"day3.database"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
