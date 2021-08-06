package com.mycode.tourapptelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = { RedisAutoConfiguration.class })
public class TourAppTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourAppTelegramBotApplication.class, args);
    }

}
