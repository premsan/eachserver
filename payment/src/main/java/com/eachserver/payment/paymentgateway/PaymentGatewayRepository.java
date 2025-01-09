package com.eachserver.payment.paymentgateway;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGatewayRepository {

    private final List<PaymentGateway> gateways;

    public PaymentGateway findById(final String id) {

        for (final PaymentGateway paymentGateway : gateways) {

            if (paymentGateway.id().equals(id)) {

                return paymentGateway;
            }
        }

        return null;
    }

    public List<PaymentGateway> findAll() {

        return gateways;
    }
}
