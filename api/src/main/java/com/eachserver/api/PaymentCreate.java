package com.eachserver.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import lombok.Getter;
import lombok.Setter;

public final class PaymentCreate {

    public static final String PATH = "/payment/payment-create";

    @Getter
    @Setter
    public static class Parameters {

        @NotBlank private String referenceId;

        @NotNull private Currency currency;

        @NotNull private BigDecimal amount;

        @NotBlank private String name;

        @NotBlank private String description;
    }
}
