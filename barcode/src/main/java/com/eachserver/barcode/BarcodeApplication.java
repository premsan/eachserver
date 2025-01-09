package com.eachserver.barcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class BarcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarcodeApplication.class, args);
    }
}
