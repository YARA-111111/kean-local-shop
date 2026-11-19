package com.kean.yelp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kean.yelp.mapper")
public class YelpRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(YelpRecommendationApplication.class, args);
    }
}
