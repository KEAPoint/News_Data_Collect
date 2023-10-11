package kea.keapoint.newsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsapiApplication.class, args);
    }

}
