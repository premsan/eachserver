package com.eachserver.json;

import com.eachserver.application.FeatureMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class JsonFormatFileController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FeatureMapping
    @GetMapping("/json/json-format-file")
    public ModelAndView getJsonFormatFile() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/json/templates/json-format-file");
        modelAndView.addObject("jsonFormatFile", new JSONFormatFile());

        return modelAndView;
    }

    @PostMapping("/json/json-format-file")
    public ModelAndView postJsonFormatFile(
            @Valid @ModelAttribute("jsonFormatFile") JSONFormatFile jsonFormatFile,
            BindingResult bindingResult) {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/json/templates/json-format-file");
        modelAndView.addObject("jsonFormatFile", jsonFormatFile);

        if (bindingResult.hasErrors()) {

            return modelAndView;
        }

        if (jsonFormatFile.getFile().isEmpty()) {

            bindingResult.rejectValue("file", "EmptyFile", "EmptyFile");

            return modelAndView;
        }

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(jsonFormatFile.getFile().getInputStream());
        } catch (final IOException e) {
            bindingResult.rejectValue("file", null, e.getMessage());

            return modelAndView;
        }

        modelAndView.addObject("output", jsonNode.toPrettyString());

        return modelAndView;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JSONFormatFile {

        @NotNull private MultipartFile file;
    }
}
