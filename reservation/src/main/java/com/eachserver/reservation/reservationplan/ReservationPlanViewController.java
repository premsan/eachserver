package com.eachserver.reservation.reservationplan;

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
public class ReservationPlanViewController {

    private final ReservationPlanRepository reservationPlanRepository;

    @FeatureMapping
    @GetMapping("/reservation/reservation-plan-view/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('RESERVATION_RESERVATION_PLAN_VIEW')")
    public ModelAndView getReservationPlanView(@PathVariable String id) {

        final Optional<ReservationPlan> optionalReservationPlan =
                reservationPlanRepository.findById(id);

        if (optionalReservationPlan.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/reservation/templates/reservation-plan-view");
        modelAndView.addObject("reservationPlan", optionalReservationPlan.get());

        return modelAndView;
    }
}
