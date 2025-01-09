package com.eachserver.grep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class GrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrepApplication.class, args);
    }
}
