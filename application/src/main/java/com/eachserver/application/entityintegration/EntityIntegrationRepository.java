package com.eachserver.application.entityintegration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
public class EntityIntegrationRepository {

    private final ApplicationContext applicationContext;

    private Map<Class<?>, List<EntityIntegration>> entityIntegrationMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {

        final Map<String, Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);

        for (final Object controller : controllers.values()) {

            final Class<?> controllerClass = AopUtils.getTargetClass(controller);

            for (final Method method : controllerClass.getDeclaredMethods()) {

                final EntityIntegrationMapping entityIntegrationMapping =
                        method.getAnnotation(EntityIntegrationMapping.class);
                final GetMapping getMapping = method.getAnnotation(GetMapping.class);

                if (entityIntegrationMapping == null
                        || getMapping == null
                        || getMapping.value().length == 0) {

                    continue;
                }

                List<EntityIntegration> entityIntegrations =
                        entityIntegrationMap.get(entityIntegrationMapping.entity());

                if (entityIntegrations == null) {

                    entityIntegrations = new ArrayList<>();
                    entityIntegrationMap.put(entityIntegrationMapping.entity(), entityIntegrations);
                }

                entityIntegrations.add(
                        new EntityIntegration(entityIntegrationMapping, getMapping.value()[0]));

                entityIntegrationMap.put(entityIntegrationMapping.entity(), entityIntegrations);
            }
        }

        for (final List<EntityIntegration> entityIntegrations : entityIntegrationMap.values()) {

            Collections.sort(
                    entityIntegrations, Comparator.comparingInt(EntityIntegration::priority));
        }
    }

    public List<EntityIntegration> findByEntity(final Class<?> entity) {

        return entityIntegrationMap.get(entity);
    }
}
