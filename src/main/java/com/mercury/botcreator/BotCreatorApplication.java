package com.mercury.botcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BotCreatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotCreatorApplication.class, args);
    }

}
