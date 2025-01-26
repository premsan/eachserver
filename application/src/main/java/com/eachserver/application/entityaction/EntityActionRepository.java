package com.eachserver.application.entityaction;

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
public class EntityActionRepository {

    private final ApplicationContext applicationContext;

    private Map<Class<?>, List<EntityAction>> entityActionMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {

        final Map<String, Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);

        for (final Object controller : controllers.values()) {

            final Class<?> controllerClass = AopUtils.getTargetClass(controller);

            for (final Method method : controllerClass.getDeclaredMethods()) {

                final EntityActionMapping entityActionMapping =
                        method.getAnnotation(EntityActionMapping.class);
                final GetMapping getMapping = method.getAnnotation(GetMapping.class);

                if (entityActionMapping == null
                        || getMapping == null
                        || getMapping.value().length == 0) {

                    continue;
                }

                List<EntityAction> entityActions =
                        entityActionMap.get(entityActionMapping.entity());

                if (entityActions == null) {

                    entityActions = new ArrayList<>();
                    entityActionMap.put(entityActionMapping.entity(), entityActions);
                }

                entityActions.add(new EntityAction(entityActionMapping, getMapping.value()[0]));

                entityActionMap.put(entityActionMapping.entity(), entityActions);
            }
        }

        for (final List<EntityAction> entityActions : entityActionMap.values()) {

            Collections.sort(entityActions, Comparator.comparingInt(EntityAction::priority));
        }
    }

    public List<EntityAction> findByEntity(final Class<?> entity) {

        return entityActionMap.get(entity);
    }
}
