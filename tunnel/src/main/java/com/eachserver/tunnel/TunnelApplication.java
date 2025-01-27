package com.eachserver.tunnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class TunnelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TunnelApplication.class, args);
    }
}
