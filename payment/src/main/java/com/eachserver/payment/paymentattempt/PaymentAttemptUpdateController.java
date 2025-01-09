package com.eachserver.payment.paymentattempt;

import com.eachserver.application.FeatureMapping;
import com.eachserver.payment.paymentgateway.PaymentAttemptStatusFetch;
import com.eachserver.payment.paymentgateway.PaymentAttemptStatusFetched;
import com.eachserver.payment.paymentgateway.PaymentGateway;
import com.eachserver.payment.paymentgateway.PaymentGatewayRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PaymentAttemptUpdateController {

    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;

    @FeatureMapping
    @GetMapping("/payment/payment-attempt-update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PAYMENT_PAYMENT_ATTEMPT_UPDATE')")
    public ModelAndView getPaymentAttemptUpdate(
            @PathVariable String id,
            @RequestParam Map<String, String> requestParams,
            final RedirectAttributes redirectAttributes) {

        final ModelAndView modelAndView = new ModelAndView();

        final Optional<PaymentAttempt> paymentAttemptOptional =
                paymentAttemptRepository.findById(id);

        if (paymentAttemptOptional.isEmpty()) {

            modelAndView.setViewName("com/eachserver/payment/templates/payment-attempt-update");

            return modelAndView;
        }

        final PaymentGateway paymentGateway =
                paymentGatewayRepository.findById(paymentAttemptOptional.get().getGatewayId());

        if (paymentGateway == null) {

            modelAndView.setViewName("com/eachserver/payment/templates/payment-attempt-update");

            return modelAndView;
        }

        Map<String, String> gatewayAttemptAttributes =
                paymentAttemptOptional.get().getGatewayAttemptAttributes();
        if (gatewayAttemptAttributes == null && !requestParams.isEmpty()) {

            gatewayAttemptAttributes = new HashMap<>();
            paymentAttemptOptional.get().setGatewayAttemptAttributes(gatewayAttemptAttributes);
        }
        if (!requestParams.isEmpty()) {

            gatewayAttemptAttributes.putAll(requestParams);
        }
        paymentAttemptRepository.save(paymentAttemptOptional.get());

        final PaymentAttemptStatusFetch paymentAttemptStatusFetch = new PaymentAttemptStatusFetch();
        paymentAttemptStatusFetch.setAttemptId(paymentAttemptOptional.get().getGatewayAttemptId());
        paymentAttemptStatusFetch.setReferenceId(paymentAttemptOptional.get().getId());
        paymentAttemptStatusFetch.setAttemptAttributes(
                paymentAttemptOptional.get().getGatewayAttemptAttributes());

        final PaymentAttemptStatusFetched paymentAttemptStatusFetched =
                paymentGateway.paymentAttemptStatusFetch(paymentAttemptStatusFetch);

        paymentAttemptOptional.get().setStatus(paymentAttemptStatusFetched.getAttemptStatus());
        paymentAttemptRepository.save(paymentAttemptOptional.get());

        redirectAttributes.addAttribute("id", paymentAttemptOptional.get().getId());
        return new ModelAndView("redirect:/payment/payment-attempt-view/{id}");
    }
}
