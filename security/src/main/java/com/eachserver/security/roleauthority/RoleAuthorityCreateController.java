package com.eachserver.security.roleauthority;

import com.eachserver.application.feature.FeatureMapping;
import com.eachserver.security.authority.Authority;
import com.eachserver.security.authority.AuthorityRepository;
import com.eachserver.security.role.Role;
import com.eachserver.security.role.RoleRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RoleAuthorityCreateController {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    private final RoleAuthorityRepository roleAuthorityRepository;

    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/security/role-authority-create")
    public ModelAndView getRoleAuthorityCreate(final RoleAuthorityCreate roleAuthorityCreate) {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/role-authority-create");
        modelAndView.addObject("roleAuthorityCreate", roleAuthorityCreate);

        return modelAndView;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/security/role-authority-create")
    public ModelAndView postRoleAuthorityCreate(
            @Valid @ModelAttribute("roleAuthorityCreate") RoleAuthorityCreate roleAuthorityCreate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        ModelAndView modelAndView = new ModelAndView();

        Role role = null;

        if (StringUtils.hasText(roleAuthorityCreate.getRoleId())) {

            role = roleRepository.findById(roleAuthorityCreate.getRoleId()).orElse(null);

            if (role == null) {

                bindingResult.rejectValue("roleId", "Invalid");
            }
        }

        Authority authority = null;

        if (StringUtils.hasText(roleAuthorityCreate.getAuthorityId())) {

            authority =
                    authorityRepository.findById(roleAuthorityCreate.getAuthorityId()).orElse(null);

            if (authority == null) {

                bindingResult.rejectValue("authorityId", "Invalid");
            }
        }

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/security/templates/role-authority-create");
            modelAndView.addObject("roleAuthorityCreate", roleAuthorityCreate);

            return modelAndView;
        }

        final RoleAuthority roleAuthority =
                roleAuthorityRepository.save(
                        new RoleAuthority(
                                UUID.randomUUID().toString(),
                                null,
                                role.getId(),
                                authority.getId(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", roleAuthority.getId());
        return new ModelAndView("redirect:/security/role-authority-view/{id}");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class RoleAuthorityCreate {

        @NotBlank private String roleId;

        @NotBlank private String authorityId;
    }
}
