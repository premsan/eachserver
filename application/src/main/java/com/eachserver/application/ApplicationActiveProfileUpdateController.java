package com.eachserver.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ApplicationActiveProfileUpdateController {

    private final Environment environment;

    private String applicationConfigurationPathFormat =
            "{0}/.eachserver/application/config/application.properties";

    @GetMapping("/application/application-active-profile-update")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or"
                    + " hasAuthority('APPLICATION_APPLICATION_ACTIVE_PROFILE_UPDATE')")
    public ModelAndView applicationActiveProfileUpdateGet() throws IOException {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/application-active-profile-update");

        final ApplicationActiveProfileUpdate applicationActiveProfileUpdate =
                new ApplicationActiveProfileUpdate();
        applicationActiveProfileUpdate.setActiveProfile(environment.getActiveProfiles()[0]);

        modelAndView.addObject("profiles", Arrays.asList("demo", "live"));
        modelAndView.addObject("applicationActiveProfileUpdate", applicationActiveProfileUpdate);
        return modelAndView;
    }

    @PostMapping("/application/application-active-profile-update")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or"
                    + " hasAuthority('APPLICATION_APPLICATION_ACTIVE_PROFILE_UPDATE')")
    public ModelAndView applicationActiveProfileUpdatePost(
            @Valid @ModelAttribute("applicationActiveProfileUpdate")
                    ApplicationActiveProfileUpdate applicationActiveProfileUpdate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes)
            throws IOException {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/module-configuration-update");

        if (bindingResult.hasErrors()) {

            modelAndView.addObject(
                    "applicationActiveProfileUpdate", applicationActiveProfileUpdate);

            return modelAndView;
        }

        final Properties properties = new Properties();
        properties.put("spring.profiles.active", applicationActiveProfileUpdate.getActiveProfile());

        final File applicationConfigurationFile =
                new File(
                        MessageFormat.format(
                                applicationConfigurationPathFormat,
                                System.getProperty("user.home")));

        if (!applicationConfigurationFile.getParentFile().exists()) {
            applicationConfigurationFile.getParentFile().mkdirs();
        }
        if (!applicationConfigurationFile.exists()) {
            applicationConfigurationFile.createNewFile();
        }
        try (final FileOutputStream fileOutputStream =
                new FileOutputStream(applicationConfigurationFile)) {

            properties.store(fileOutputStream, null);
        }

        return new ModelAndView("redirect:/application/application-restart-view");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class ApplicationActiveProfileUpdate {

        @NotNull private String activeProfile;
    }
}
