package com.eachserver.payment.paymentgateway;

import com.eachserver.payment.paymentattempt.PaymentAttemptStatus;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AdyenPaymentGateway implements PaymentGateway {

    private final RestClient restClient;
    private final PaymentGatewayConfiguration paymentGatewayConfiguration;

    @Override
    public String id() {

        return "adyen";
    }

    @Override
    public PaymentAttemptCreated paymentAttemptCreate(
            final PaymentAttemptCreate paymentAttemptCreate) {

        final PaymentGatewayConfiguration.Configuration configuration =
                paymentGatewayConfiguration.getGateway().get(id());

        final HashMap<String, Object> body = new HashMap<>();

        body.put("merchantAccount", configuration.getAccountId());

        final HashMap<String, Object> amount = new HashMap<>();
        amount.put(
                "value",
                paymentAttemptCreate
                        .getAmount()
                        .multiply(
                                BigDecimal.TEN.pow(
                                        paymentAttemptCreate
                                                .getCurrency()
                                                .getDefaultFractionDigits())));
        amount.put("currency", paymentAttemptCreate.getCurrency().getCurrencyCode());
        body.put("amount", amount);

        body.put(
                "returnUrl",
                "http://localhost:8080/payment/payment-attempt-update/"
                        + paymentAttemptCreate.getReferenceId());
        body.put("reference", paymentAttemptCreate.getReferenceId());
        body.put("mode", "hosted");
        body.put("themeId", configuration.getThemeId());

        final Map<String, Object> response =
                restClient
                        .method(HttpMethod.POST)
                        .uri(configuration.getCreatePaymentUrl())
                        .header("content-type", "application/json")
                        .header("x-API-key", configuration.getClientKeySecret())
                        .body(body)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        final PaymentAttemptCreated paymentAttemptCreated = new PaymentAttemptCreated();
        paymentAttemptCreated.setId((String) response.get("id"));
        paymentAttemptCreated.setUrl((String) response.get("url"));

        return paymentAttemptCreated;
    }

    @Override
    public PaymentAttemptStatusFetched paymentAttemptStatusFetch(
            final PaymentAttemptStatusFetch paymentAttemptStatusFetch) {

        final PaymentGatewayConfiguration.Configuration configuration =
                paymentGatewayConfiguration.getGateway().get(id());

        final Map<String, String> response =
                restClient
                        .method(HttpMethod.GET)
                        .uri(
                                UriComponentsBuilder.fromUriString(
                                                configuration.getFetchPaymentStatusUrl())
                                        .queryParam(
                                                "sessionResult",
                                                paymentAttemptStatusFetch
                                                        .getAttemptAttributes()
                                                        .get("sessionResult"))
                                        .buildAndExpand(paymentAttemptStatusFetch.getAttemptId())
                                        .toUri())
                        .header("x-API-key", configuration.getClientKeySecret())
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        final PaymentAttemptStatusFetched paymentAttemptStatusFetched =
                new PaymentAttemptStatusFetched();
        paymentAttemptStatusFetched.setAttemptStatus(map(response.get("status")));
        return paymentAttemptStatusFetched;
    }

    private PaymentAttemptStatus map(final String status) {

        switch (status) {
            case "completed":
                return PaymentAttemptStatus.COMPLETED;
            case "paymentPending":
                return PaymentAttemptStatus.PENDING;
            case "canceled":
                return PaymentAttemptStatus.CANCELLED;
            case "expired":
                return PaymentAttemptStatus.EXPIRED;
            default:
                return null;
        }
    }
}
