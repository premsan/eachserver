package com.eachserver.payment.payment;

import com.eachserver.application.feature.FeatureMapping;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class PaymentViewController {

    private final PaymentRepository paymentRepository;

    @FeatureMapping
    @GetMapping("/payment/payment-view/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PAYMENT_PAYMENT_VIEW')")
    public ModelAndView getPaymentView(@PathVariable String id) {

        final Optional<Payment> optionalPayment = paymentRepository.findById(id);

        if (optionalPayment.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/payment/templates/payment-view");
        modelAndView.addObject("payment", optionalPayment.get());

        return modelAndView;
    }
}
