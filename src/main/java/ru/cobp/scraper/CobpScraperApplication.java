package ru.cobp.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CobpScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobpScraperApplication.class, args);
    }

}
