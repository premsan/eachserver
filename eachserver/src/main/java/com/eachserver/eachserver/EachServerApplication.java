package com.eachserver.eachserver;

import com.eachserver.application.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eachserver")
public class EachServerApplication extends BaseApplication {

    public static void main(String[] args) {

        primarySource = EachServerApplication.class;
        context = SpringApplication.run(EachServerApplication.class, args);
    }
}
