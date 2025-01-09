package com.eachserver.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ApplicationConfigurationUpdateController {

    private final Environment environment;

    private String profileConfigurationPathFormat =
            "{0}/.eachserver/application/config/application-{1}.properties";

    @GetMapping("/application/application-configuration-update")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or"
                    + " hasAuthority('APPLICATION_APPLICATION_CONFIGURATION_UPDATE')")
    public ModelAndView applicationConfigurationUpdateGet() {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/application-configuration-update");

        final Map<String, String> properties = new HashMap<>();

        final String configurablePropertiesStr =
                environment.getProperty(
                        "com.eachserver.application.configurable-properties", (String) null);

        if (StringUtils.hasText(configurablePropertiesStr)) {

            final String[] configurableProperties = configurablePropertiesStr.split(",");

            for (final String key : configurableProperties) {

                properties.put(key, environment.getProperty(key));
            }
        }

        if (properties.isEmpty()) {

            return modelAndView;
        }

        final ApplicationConfigurationUpdate applicationConfigurationUpdate =
                new ApplicationConfigurationUpdate();
        applicationConfigurationUpdate.setProperties(properties);

        modelAndView.addObject("applicationConfigurationUpdate", applicationConfigurationUpdate);
        return modelAndView;
    }

    @PostMapping("/application/application-configuration-update")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or"
                    + " hasAuthority('APPLICATION_APPLICATION_CONFIGURATION_UPDATE')")
    public ModelAndView applicationConfigurationUpdatePost(
            @Valid @ModelAttribute("applicationConfigurationUpdate")
                    ApplicationConfigurationUpdate applicationConfigurationUpdate,
            BindingResult bindingResult)
            throws IOException {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/application-configuration-update");

        if (bindingResult.hasErrors()) {

            modelAndView.addObject(
                    "applicationConfigurationUpdate", applicationConfigurationUpdate);

            return modelAndView;
        }

        final Properties properties = new Properties();
        properties.putAll(applicationConfigurationUpdate.getProperties());

        final File profileConfigurationFile =
                new File(
                        MessageFormat.format(
                                profileConfigurationPathFormat,
                                System.getProperty("user.home"),
                                environment.getActiveProfiles()[0]));

        if (!profileConfigurationFile.getParentFile().exists()) {
            profileConfigurationFile.getParentFile().mkdirs();
        }
        if (!profileConfigurationFile.exists()) {
            profileConfigurationFile.createNewFile();
        }
        try (final FileOutputStream fileOutputStream =
                new FileOutputStream(profileConfigurationFile)) {

            properties.store(fileOutputStream, null);
        }

        return new ModelAndView("redirect:/application/application-restart-view");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class ApplicationConfigurationUpdate {

        @NotNull private Map<String, String> properties;
    }
}
