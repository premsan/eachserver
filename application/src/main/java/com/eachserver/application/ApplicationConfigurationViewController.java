package com.eachserver.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ApplicationConfigurationViewController {

    private final Environment environment;

    @GetMapping("/application/application-configuration-view")
    public ModelAndView applicationConfigurationViewGet() {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/application-configuration-view");

        final ApplicationConfigurationView applicationConfigurationView =
                new ApplicationConfigurationView();
        applicationConfigurationView.setActiveProfile(environment.getActiveProfiles()[0]);

        modelAndView.addObject("applicationConfigurationView", applicationConfigurationView);
        return modelAndView;
    }

    @Getter
    @Setter
    private static class ApplicationConfigurationView {

        private String activeProfile;
    }
}
