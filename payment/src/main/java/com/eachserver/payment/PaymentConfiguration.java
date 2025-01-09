package com.eachserver.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
