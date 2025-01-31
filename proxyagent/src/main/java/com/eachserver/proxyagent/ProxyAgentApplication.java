package com.eachserver.proxyagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class ProxyAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyAgentApplication.class, args);
    }
}
