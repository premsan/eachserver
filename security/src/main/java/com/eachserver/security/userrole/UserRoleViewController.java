package com.eachserver.security.userrole;

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
public class UserRoleViewController {

    private final UserRoleRepository userRoleRepository;

    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/security/user-role-view/{id}")
    public ModelAndView getUserRoleView(@PathVariable final String id) {

        Optional<UserRole> userRoleOptional = userRoleRepository.findById(id);

        if (userRoleOptional.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/user-role-view");
        modelAndView.addObject("userRole", userRoleOptional.get());

        return modelAndView;
    }
}
