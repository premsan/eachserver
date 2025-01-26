package com.eachserver.security.user;

import com.eachserver.application.feature.FeatureMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class UserIndexController {

    private final UserRepository userRepository;

    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/security/user-index")
    public ModelAndView getUserIndex() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/user-index");
        modelAndView.addObject("users", userRepository.findAll());

        return modelAndView;
    }
}
