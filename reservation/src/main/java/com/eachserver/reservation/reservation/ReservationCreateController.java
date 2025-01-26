package com.eachserver.reservation.reservation;

import com.eachserver.application.feature.FeatureMapping;
import com.eachserver.reservation.reservationplan.ReservationPlan;
import com.eachserver.reservation.reservationplan.ReservationPlanRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
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
public class ReservationCreateController {

    private final ReservationRepository reservationRepository;
    private final ReservationPlanRepository reservationPlanRepository;

    @FeatureMapping
    @GetMapping("/reservation/reservation-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_CREATE')")
    public ModelAndView getReservationCreate() {

        final ModelAndView model =
                new ModelAndView("com/eachserver/reservation/templates/reservation-create");
        model.addObject("reservationCreate", new ReservationCreate());

        return model;
    }

    @PostMapping("/reservation/reservation-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_CREATE')")
    public ModelAndView postReservationCreate(
            @Valid @ModelAttribute("reservationCreate") final ReservationCreate reservationCreate,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        final ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/reservation/templates/reservation-create");
            modelAndView.addObject("reservationCreate", reservationCreate);

            return modelAndView;
        }

        if (!reservationCreate.getEndAt().isAfter(reservationCreate.getStartAt())) {

            bindingResult.rejectValue("startAt", null, "endAt isNotAfter startAt");
            bindingResult.rejectValue("endAt", null, "endAt isNotAfter startAt");

            modelAndView.setViewName("com/eachserver/reservation/templates/reservation-create");
            modelAndView.addObject("reservationCreate", reservationCreate);

            return modelAndView;
        }

        final Optional<ReservationPlan> optionalReservationPlan =
                reservationPlanRepository.findById(reservationCreate.getPlanId());

        if (optionalReservationPlan.isEmpty()) {

            bindingResult.rejectValue("planId", null, "Invalid planId");

            modelAndView.setViewName("com/eachserver/reservation/templates/reservation-create");
            modelAndView.addObject("reservationCreate", reservationCreate);

            return modelAndView;
        }

        final ZoneId zoneId = ZoneId.of(optionalReservationPlan.get().getZoneId());
        final Long startAt = reservationCreate.getStartAt().atZone(zoneId).toEpochSecond();
        final Long endAt = reservationCreate.getEndAt().atZone(zoneId).toEpochSecond();

        if (reservationRepository.existsByEndAtGreaterThanAndStartAtLessThan(startAt, endAt)) {

            bindingResult.rejectValue("startAt", null, "reservation exists");
            bindingResult.rejectValue("endAt", null, "reservation exists");

            modelAndView.setViewName("com/eachserver/reservation/templates/reservation-create");
            modelAndView.addObject("reservationCreate", reservationCreate);

            return modelAndView;
        }

        final Reservation reservation =
                reservationRepository.save(
                        new Reservation(
                                UUID.randomUUID().toString(),
                                null,
                                optionalReservationPlan.get().getId(),
                                reservationCreate.getName(),
                                reservationCreate.getDescription(),
                                startAt,
                                endAt,
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", reservation.getId());
        return new ModelAndView("redirect:/reservation/reservation-view/{id}");
    }

    @Getter
    @Setter
    private static class ReservationCreate {

        @NotBlank private String planId;

        @NotBlank private String name;

        @NotBlank private String description;

        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startAt;

        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endAt;
    }
}
