package com.eachserver.payment.paymentgateway;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.eachserver.payment")
public class PaymentGatewayConfiguration {

    private Map<String, Configuration> gateway;

    @Getter
    @Setter
    public static class Configuration {

        private String createPaymentUrl;

        private String fetchPaymentStatusUrl;

        private String accountId;

        private String clientKeyId;

        private String clientKeySecret;

        private String themeId;
    }
}
