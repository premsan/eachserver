package com.eachserver.application;

import com.eachserver.application.feature.FeatureMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApplicationRestartViewController {

    @FeatureMapping
    @GetMapping("/application/application-restart-view")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('APPLICATION_APPLICATION_RESTART_VIEW')")
    public ModelAndView applicationRestartGet() {

        return new ModelAndView("com/eachserver/application/templates/application-restart-view");
    }

    @PostMapping("/application/application-restart-view")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('APPLICATION_APPLICATION_RESTART_VIEW')")
    public ModelAndView applicationRestartPost() {

        BaseApplication.restartApplication();
        return new ModelAndView("redirect:/");
    }
}
