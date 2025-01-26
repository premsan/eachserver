package com.eachserver.security.roleauthority;

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
public class RoleAuthorityViewController {

    private final RoleAuthorityRepository roleAuthorityRepository;

    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/security/role-authority-view/{id}")
    public ModelAndView getRoleAuthorityView(@PathVariable final String id) {

        Optional<RoleAuthority> roleAuthorityOptional = roleAuthorityRepository.findById(id);

        if (roleAuthorityOptional.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/role-authority-view");
        modelAndView.addObject("roleAuthority", roleAuthorityOptional.get());

        return modelAndView;
    }
}
