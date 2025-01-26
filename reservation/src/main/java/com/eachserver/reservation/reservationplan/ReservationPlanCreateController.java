package com.eachserver.reservation.reservationplan;

import com.eachserver.application.feature.FeatureMapping;
import com.eachserver.reservation.reservationresource.ReservationResource;
import com.eachserver.reservation.reservationresource.ReservationResourceRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
public class ReservationPlanCreateController {

    private final ReservationResourceRepository reservationResourceRepository;
    private final ReservationPlanRepository reservationPlanRepository;

    @FeatureMapping
    @GetMapping("/reservation/reservation-plan-create")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_PLAN_CREATE')")
    public ModelAndView getReservationPlanCreate() {

        final ModelAndView model =
                new ModelAndView("com/eachserver/reservation/templates/reservation-plan-create");
        model.addObject("reservationPlanCreate", new ReservationPlanCreate());

        return model;
    }

    @PostMapping("/reservation/reservation-plan-create")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_PLAN_CREATE')")
    public ModelAndView postReservationPlanCreate(
            @Valid @ModelAttribute("reservationPlanCreate")
                    final ReservationPlanCreate reservationPlanCreate,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName(
                    "com/eachserver/reservation/templates/reservation-plan-create");
            modelAndView.addObject("reservationPlanCreate", reservationPlanCreate);

            return modelAndView;
        }

        final Optional<ReservationResource> optionalReservationResource =
                reservationResourceRepository.findById(reservationPlanCreate.getResourceId());

        if (optionalReservationResource.isEmpty()) {

            bindingResult.rejectValue("resourceId", null, "Invalid resourceId");

            modelAndView.setViewName(
                    "com/eachserver/reservation/templates/reservation-plan-create");
            modelAndView.addObject("reservationPlanCreate", reservationPlanCreate);

            return modelAndView;
        }

        final ReservationPlan reservationPlan =
                reservationPlanRepository.save(
                        new ReservationPlan(
                                UUID.randomUUID().toString(),
                                null,
                                optionalReservationResource.get().getId(),
                                reservationPlanCreate.getChronoUnit(),
                                reservationPlanCreate.getMinUnit(),
                                reservationPlanCreate.getMaxUnit(),
                                reservationPlanCreate.getZoneId().getId(),
                                reservationPlanCreate
                                        .getStartAt()
                                        .atZone(reservationPlanCreate.getZoneId())
                                        .toEpochSecond(),
                                reservationPlanCreate
                                        .getEndAt()
                                        .atZone(reservationPlanCreate.getZoneId())
                                        .toEpochSecond(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", reservationPlan.getId());
        return new ModelAndView("redirect:/reservation/reservation-plan-view/{id}");
    }

    @Getter
    @Setter
    private static class ReservationPlanCreate {

        @NotBlank private String resourceId;

        @NotNull private ChronoUnit chronoUnit;

        @NotNull private Long minUnit;

        @NotNull private Long maxUnit;

        @NotNull private ZoneId zoneId;

        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startAt;

        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endAt;
    }
}
