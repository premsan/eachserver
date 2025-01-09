package com.eachserver.payment.paymentgateway;

import com.eachserver.payment.paymentattempt.PaymentAttemptStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentAttemptStatusFetched {

    private PaymentAttemptStatus attemptStatus;
}
