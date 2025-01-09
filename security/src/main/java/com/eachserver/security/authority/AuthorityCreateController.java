package com.eachserver.security.authority;

import com.eachserver.application.FeatureMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class AuthorityCreateController {

    private final AuthorityRepository authorityRepository;

    @FeatureMapping
    @GetMapping("/security/authority-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('SECURITY_AUTHORITY_CREATE')")
    public ModelAndView getAuthorityCreate() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/authority-create");
        modelAndView.addObject("authorityCreate", new AuthorityCreate());

        return modelAndView;
    }

    @PostMapping("/security/authority-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('SECURITY_AUTHORITY_CREATE')")
    public ModelAndView postAuthorityCreate(
            @Valid @ModelAttribute("authorityCreate") AuthorityCreate authorityCreate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentSecurityContext SecurityContext securityContext) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/security/templates/authority-create");
            modelAndView.addObject("authorityCreate", authorityCreate);

            return modelAndView;
        }

        final Authority authority =
                authorityRepository.save(
                        new Authority(
                                UUID.randomUUID().toString(),
                                null,
                                authorityCreate.getName(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", authority.getId());
        return new ModelAndView("redirect:/security/authority-view/{id}");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthorityCreate {

        @NotBlank private String name;
    }
}
