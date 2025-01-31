package com.eachserver.security.user;

import com.eachserver.application.feature.FeatureMapping;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class SubUserCreateController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @FeatureMapping
    @GetMapping("/security/sub-user-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('SUB_USER_CREATE')")
    public ModelAndView subUserCreateGet() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/sub-user-create");

        return modelAndView;
    }

    @FeatureMapping
    @PostMapping("/security/sub-user-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('SUB_USER_CREATE')")
    public ModelAndView subUserCreatePost(
            @CurrentSecurityContext final SecurityContext securityContext) {

        final String password = UUID.randomUUID().toString();

        final User user =
                userRepository.save(
                        new User(
                                UUID.randomUUID().toString(),
                                null,
                                securityContext.getAuthentication().getName(),
                                null,
                                passwordEncoder.encode(password),
                                false,
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        final SubUserCreated subUserCreated = new SubUserCreated();
        subUserCreated.setId(user.getId());
        subUserCreated.setPassword(password);

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/security/templates/sub-user-created");

        modelAndView.addObject("subUserCreated", subUserCreated);

        return modelAndView;
    }

    @Getter
    @Setter
    private static class SubUserCreated {

        private String id;

        private String password;
    }
}
