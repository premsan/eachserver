package com.eachserver.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ModuleConfigurationUpdateController {

    private final Environment environment;

    private String baseConfigurationPathFormat = "com/eachserver/{0}/config/{0}.properties";

    private String profileConfigurationPathFormat =
            "{0}/.eachserver/{1}/config/{1}-{2}.properties";

    @GetMapping("/application/module-configuration-update/{moduleId}")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('APPLICATION_MODULE_CONFIGURATION_UPDATE')")
    public ModelAndView moduleConfigurationUpdateGet(@PathVariable String moduleId)
            throws IOException {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/module-configuration-update");
        modelAndView.addObject("moduleId", moduleId);

        final Map<String, String> properties = new HashMap<>();

        try (final InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                MessageFormat.format(baseConfigurationPathFormat, moduleId))) {

            final Properties loadedProperties = new Properties();

            if (Objects.nonNull(inputStream)) {

                loadedProperties.load(inputStream);
            }

            for (final String key : loadedProperties.stringPropertyNames()) {

                if (key.startsWith("com.eachserver")) {
                    properties.put(key, environment.getProperty(key));
                }
            }
        }

        if (properties.isEmpty()) {

            return modelAndView;
        }

        ModuleConfigurationUpdate moduleConfigurationUpdate = new ModuleConfigurationUpdate();
        moduleConfigurationUpdate.setProperties(properties);

        modelAndView.addObject("moduleConfigurationUpdate", moduleConfigurationUpdate);
        return modelAndView;
    }

    @PostMapping("/application/module-configuration-update/{moduleId}")
    @PreAuthorize(
            "hasAuthority('ROLE_ADMIN') or hasAuthority('APPLICATION_MODULE_CONFIGURATION_UPDATE')")
    public ModelAndView moduleConfigurationUpdatePost(
            @PathVariable String moduleId,
            @Valid @ModelAttribute("moduleConfigurationUpdate")
                    ModuleConfigurationUpdate moduleConfigurationUpdate,
            BindingResult bindingResult)
            throws IOException {

        final ModelAndView modelAndView =
                new ModelAndView(
                        "com/eachserver/application/templates/module-configuration-update");

        if (bindingResult.hasErrors()) {

            modelAndView.addObject("moduleId", moduleId);
            modelAndView.addObject("moduleConfigurationUpdate", moduleConfigurationUpdate);

            return modelAndView;
        }

        final Properties properties = new Properties();
        properties.putAll(moduleConfigurationUpdate.getProperties());

        final File profileConfigurationFile =
                new File(
                        MessageFormat.format(
                                profileConfigurationPathFormat,
                                System.getProperty("user.home"),
                                moduleId,
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
    public class ModuleConfigurationUpdate {

        @NotNull private Map<String, String> properties;
    }
}
