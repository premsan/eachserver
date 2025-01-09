package com.eachserver.application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ApplicationHomeController {

    private final Pattern authorityPattern = Pattern.compile("hasAuthority\\('(.*?)'\\)");

    private final ApplicationContext applicationContext;
    private final Map<String, List<Feature>> moduleFeatures = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {

        final Map<String, Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);

        for (final Object controller : controllers.values()) {

            final Class<?> controllerClass = AopUtils.getTargetClass(controller);

            for (final Method method : controllerClass.getDeclaredMethods()) {

                final FeatureMapping featureMapping = method.getAnnotation(FeatureMapping.class);
                final GetMapping featureGetMapping = method.getAnnotation(GetMapping.class);

                if (featureMapping == null
                        || featureGetMapping == null
                        || featureGetMapping.value().length == 0) {

                    continue;
                }

                final String module = controllerClass.getPackageName().split("\\.")[2];
                final PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

                final Feature feature = new Feature();
                feature.setModule(module);
                feature.setPath(featureGetMapping.value()[0]);
                feature.setPriority(featureMapping.priority());
                feature.setMessageCode(
                        controllerClass.getSimpleName().concat(".").concat(method.getName()));

                if (Objects.nonNull(preAuthorize)) {

                    final List<String> authorities = new ArrayList<>();

                    final Matcher authorityMatcher = authorityPattern.matcher(preAuthorize.value());
                    while (authorityMatcher.find()) {

                        authorities.add(authorityMatcher.group(1));
                    }
                    feature.setPreAuthorizeAuthorities(authorities);
                }

                List<Feature> moduleFeatures = this.moduleFeatures.get(module);
                if (moduleFeatures == null) {

                    moduleFeatures = new ArrayList<>();
                    this.moduleFeatures.put(feature.getModule(), moduleFeatures);
                }
                moduleFeatures.add(feature);
            }
        }

        for (final List<Feature> moduleFeatures : moduleFeatures.values()) {

            Collections.sort(moduleFeatures, Comparator.comparing(Feature::getPriority));
        }
    }

    @GetMapping
    public ModelAndView homeGet() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/application/templates/application-home");
        modelAndView.addObject("moduleFeatures", moduleFeatures);

        return modelAndView;
    }
}
