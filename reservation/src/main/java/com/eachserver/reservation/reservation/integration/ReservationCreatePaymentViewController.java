package com.eachserver.reservation.reservation.integration;

import com.eachserver.api.PaymentCreate;
import com.eachserver.application.entityintegration.EntityIntegrationMapping;
import com.eachserver.partner.PartnerTokenProvider;
import com.eachserver.partner.api.PartnerAPI;
import com.eachserver.partner.api.PartnerAPIRepository;
import com.eachserver.reservation.reservation.Reservation;
import com.eachserver.reservation.reservation.ReservationRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
public class ReservationCreatePaymentViewController {

    private final ReservationRepository reservationRepository;
    private final PartnerAPIRepository partnerAPIRepository;

    private final PartnerTokenProvider partnerTokenProvider;

    @EntityIntegrationMapping(entity = Reservation.class)
    @GetMapping("/reservation/reservation-create-payment-view/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_VIEW')")
    public ModelAndView getReservationCreatePaymentView(@PathVariable String id) {

        final Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final List<PartnerAPI> partnerAPIs = partnerAPIRepository.findByPath(PaymentCreate.PATH);

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/reservation/templates/reservation-create-payment-view");

        modelAndView.addObject("reservation", optionalReservation.get());
        modelAndView.addObject("partnerAPIs", partnerAPIs);

        return modelAndView;
    }

    @PostMapping("/reservation/reservation-create-payment-view/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_VIEW')")
    public Object postReservationCreatePaymentView(
            @PathVariable String id, @RequestParam String partnerAPIId) throws JOSEException {

        final Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final Optional<PartnerAPI> optionalPartnerAPI = partnerAPIRepository.findById(partnerAPIId);

        if (optionalPartnerAPI.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        PaymentCreate.Parameters parameters = new PaymentCreate.Parameters();
        parameters.setReferenceId(optionalReservation.get().getId());

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(optionalPartnerAPI.get().getHost())
                        .path(optionalPartnerAPI.get().getPath())
                        .queryParam(
                                "signedToken",
                                partnerTokenProvider
                                        .createToken(
                                                optionalPartnerAPI.get(),
                                                new JWTClaimsSet.Builder()
                                                        .claim("parameters", parameters))
                                        .serialize());

        final RedirectView redirectView = new RedirectView();
        redirectView.setUrl(uriComponentsBuilder.toUriString());

        return redirectView;
    }
}
