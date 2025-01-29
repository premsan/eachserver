package com.eachserver.tunnelserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class TunnelServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TunnelServerApplication.class, args);
    }
}
