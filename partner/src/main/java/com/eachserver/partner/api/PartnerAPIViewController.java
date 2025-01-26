package com.eachserver.partner.api;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class PartnerAPIViewController {

    private final PartnerAPIRepository partnerAPIRepository;

    @GetMapping("/partner/partner-api-view/{id}")
    public ModelAndView getPartnerAPIView(@PathVariable final String id) {

        final Optional<PartnerAPI> optionalPartnerAPI = partnerAPIRepository.findById(id);

        if (optionalPartnerAPI.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/partner/templates/partner-api-view");
        modelAndView.addObject("partnerAPI", optionalPartnerAPI.get());

        return modelAndView;
    }
}
