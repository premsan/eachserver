package com.eachserver.grep;

import com.eachserver.application.feature.FeatureMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Profile("!demo")
@RequiredArgsConstructor
public class GrepHostFileController {

    private static final Integer DEFAULT_MAX_TARGET_LENGTH = 8192;
    private static final String VARIABLE_REPLACEMENT = "(.*)";

    private final Pattern variablePattern = Pattern.compile("\\[(.*?)]");

    @FeatureMapping
    @GetMapping("/grep/grep-host-file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('GREP_GREP_HOST_FILE')")
    public ModelAndView getGrepHostFile() {

        ModelAndView model = new ModelAndView("com/eachserver/grep/templates/grep-host-file");
        model.addObject("grepHostFile", new GrepHostFile());

        return model;
    }

    @PostMapping("/grep/grep-host-file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('GREP_GREP_HOST_FILE')")
    public ModelAndView postGrepHostFile(
            @Valid @ModelAttribute("grepHostFile") GrepHostFile grepHostFile,
            BindingResult bindingResult) {

        final ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/grep/templates/grep-host-file");
            modelAndView.addObject("grepHostFile", grepHostFile);

            return modelAndView;
        }

        final Matcher matcher =
                variablePattern.matcher(
                        grepHostFile.getPattern().replaceAll("[\\\\<({^\\-=$!|})?*+.>]", "\\\\$0"));

        final List<String> variables = new ArrayList<>();
        final List<List<String>> matches = new ArrayList<>();

        while (matcher.find()) {

            variables.add(matcher.group(1));
        }

        final Pattern variableMatcher = Pattern.compile(matcher.replaceAll(VARIABLE_REPLACEMENT));

        try (final BufferedReader reader =
                new BufferedReader(new FileReader(grepHostFile.getPath()))) {

            final StringBuilder stringBuilder = new StringBuilder();

            while (true) {

                final int ch = reader.read();

                if (ch == '\r'
                        || ch == '\n'
                        || ch == -1
                        || stringBuilder.length() == DEFAULT_MAX_TARGET_LENGTH) {

                    final Matcher lineMatcher = variableMatcher.matcher(stringBuilder);

                    while (lineMatcher.find()) {

                        final List<String> groups = new ArrayList<>();

                        for (int group = 1; group <= lineMatcher.groupCount(); group++) {

                            groups.add(lineMatcher.group(group));
                        }
                        matches.add(groups);
                    }

                    if (ch == -1) {

                        break;
                    }
                    stringBuilder.setLength(0);
                }
                stringBuilder.append((char) ch);
            }
        } catch (final IOException e) {

            bindingResult.rejectValue("path", null, e.getMessage());
            modelAndView.setViewName("com/eachserver/grep/templates/grep-host-file");
            modelAndView.addObject("grepHostFile", grepHostFile);

            return modelAndView;
        }

        modelAndView.addObject(grepHostFile);
        modelAndView.addObject("variables", variables);
        modelAndView.addObject("matches", matches);
        modelAndView.setViewName("com/eachserver/grep/templates/grep-host-file");

        return modelAndView;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GrepHostFile {

        @NotBlank private String path;

        @NotBlank private String pattern;
    }
}
