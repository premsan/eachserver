package com.eachserver.payment.payment;

import com.eachserver.application.FeatureMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PaymentCreateController {

    private final PaymentRepository paymentRepository;

    @FeatureMapping
    @GetMapping("/payment/payment-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PAYMENT_PAYMENT_CREATE')")
    public ModelAndView getPaymentCreate() {

        final ModelAndView model =
                new ModelAndView("com/eachserver/payment/templates/payment-create");
        model.addObject(
                "CurrencyCode",
                Currency.getAvailableCurrencies().stream()
                        .map(Currency::getCurrencyCode)
                        .sorted(String::compareTo));
        model.addObject("paymentCreate", new PaymentCreate());

        return model;
    }

    @PostMapping("/payment/payment-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PAYMENT_PAYMENT_CREATE')")
    public ModelAndView postPaymentCreate(
            @Valid @ModelAttribute("paymentCreate") final PaymentCreate paymentCreate,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        final ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/payment/templates/payment-create");
            modelAndView.addObject("paymentCreate", paymentCreate);

            return modelAndView;
        }

        final Payment payment =
                paymentRepository.save(
                        new Payment(
                                UUID.randomUUID().toString(),
                                null,
                                paymentCreate.getReferenceId(),
                                paymentCreate.getCurrency().getCurrencyCode(),
                                paymentCreate.getAmount(),
                                paymentCreate.getName(),
                                paymentCreate.getDescription(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", payment.getId());
        return new ModelAndView("redirect:/payment/payment-view/{id}");
    }

    @Getter
    @Setter
    private static class PaymentCreate {

        @NotBlank private String referenceId;

        @NotNull private Currency currency;

        @NotNull private BigDecimal amount;

        @NotBlank private String name;

        @NotBlank private String description;
    }
}
