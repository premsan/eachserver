package com.eachserver.payment.paymentgateway;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentAttemptStatusFetch {

    private String attemptId;

    private String referenceId;

    private Map<String, String> attemptAttributes;
}
