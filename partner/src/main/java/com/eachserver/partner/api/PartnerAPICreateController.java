package com.eachserver.partner.api;

import com.eachserver.application.feature.FeatureMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class PartnerAPICreateController {

    private final PartnerAPIRepository partnerAPIRepository;

    @FeatureMapping
    @GetMapping("/partner/partner-api-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PARTNER_PARTNER_API_CREATE')")
    public ModelAndView getPartnerAPICreate() {

        final ModelAndView model =
                new ModelAndView("com/eachserver/partner/templates/partner-api-create");
        model.addObject("partnerAPICreate", new PartnerAPICreate());

        return model;
    }

    @PostMapping("/partner/partner-api-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PARTNER_PARTNER_API_CREATE')")
    public ModelAndView postPartnerAPICreate(
            @Valid @ModelAttribute("partnerAPICreate") final PartnerAPICreate partnerAPICreate,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        final ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/partner/templates/partner-api-create");
            modelAndView.addObject("partnerAPICreate", partnerAPICreate);

            return modelAndView;
        }

        final PartnerAPI partnerAPI =
                partnerAPIRepository.save(
                        new PartnerAPI(
                                UUID.randomUUID().toString(),
                                null,
                                partnerAPICreate.getHost(),
                                partnerAPICreate.getPath(),
                                partnerAPICreate.isInbound(),
                                partnerAPICreate.isOutbound(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", partnerAPI.getId());
        return new ModelAndView("redirect:/partner/partner-api-view/{id}");
    }

    @Getter
    @Setter
    private static class PartnerAPICreate {

        @NotBlank private String host;

        @NotBlank private String path;

        private boolean inbound;

        private boolean outbound;
    }
}
