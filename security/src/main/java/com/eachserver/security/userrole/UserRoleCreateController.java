package com.eachserver.security.userrole;

import com.eachserver.application.FeatureMapping;
import com.eachserver.security.role.Role;
import com.eachserver.security.role.RoleRepository;
import com.eachserver.security.user.User;
import com.eachserver.security.user.UserRepository;
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
public class UserRoleCreateController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/security/user-role-create")
    public ModelAndView getUserRoleCreate(final UserRoleCreate userRoleCreate) {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/user-role-create");
        modelAndView.addObject("userRoleCreate", userRoleCreate);

        return modelAndView;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/security/user-role-create")
    public ModelAndView postUserRoleCreate(
            @Valid @ModelAttribute("userRoleCreate") UserRoleCreate userRoleCreate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        ModelAndView modelAndView = new ModelAndView();

        User user = null;

        if (StringUtils.hasText(userRoleCreate.getUserId())) {

            user = userRepository.findById(userRoleCreate.getUserId()).orElse(null);

            if (user == null) {

                bindingResult.rejectValue("userId", "Invalid");
            }
        }

        Role role = null;

        if (StringUtils.hasText(userRoleCreate.getRoleId())) {

            role = roleRepository.findById(userRoleCreate.getRoleId()).orElse(null);

            if (role == null) {

                bindingResult.rejectValue("roleId", "Invalid");
            }
        }

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/security/templates/user-role-create");
            modelAndView.addObject("userRoleCreate", userRoleCreate);

            return modelAndView;
        }

        final UserRole userRole =
                userRoleRepository.save(
                        new UserRole(
                                UUID.randomUUID().toString(),
                                null,
                                user.getId(),
                                role.getId(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", userRole.getId());
        return new ModelAndView("redirect:/security/user-role-view/{id}");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class UserRoleCreate {

        @NotBlank private String userId;

        @NotBlank private String roleId;
    }
}
