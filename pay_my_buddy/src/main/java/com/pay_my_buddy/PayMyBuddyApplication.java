package com.pay_my_buddy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayMyBuddyApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                              .filename("DB.env") 
                              .load();

        System.setProperty("DB_URL", dotenv.get("spring.datasource.url"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(PayMyBuddyApplication.class, args);
    }
}
