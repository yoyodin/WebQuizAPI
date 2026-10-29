package org.example.webquizapi;

import org.example.webquizapi.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class WebQuizApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebQuizApiApplication.class, args);
    }

}
