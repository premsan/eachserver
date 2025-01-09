package com.eachserver.security.permissionevaluator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PermissionEvaluators implements PermissionEvaluator {

    private final Map<Class<?>, ObjectPermissionEvaluator<?>> objectPermissionEvaluators =
            new HashMap<>();
    private final Map<String, TypePermissionEvaluator> typePermissionEvaluators = new HashMap<>();

    public PermissionEvaluators(
            final List<ObjectPermissionEvaluator<?>> objectPermissionEvaluators,
            final List<TypePermissionEvaluator> typePermissionEvaluators) {
        for (final ObjectPermissionEvaluator<?> evaluator : objectPermissionEvaluators) {
            this.objectPermissionEvaluators.put(evaluator.evaluates(), evaluator);
        }
        for (final TypePermissionEvaluator evaluator : typePermissionEvaluators) {
            this.typePermissionEvaluators.put(evaluator.evaluates(), evaluator);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hasPermission(
            final Authentication authentication,
            final Object targetDomainObject,
            final Object permission) {

        final ObjectPermissionEvaluator evaluator =
                objectPermissionEvaluators.get(targetDomainObject.getClass());

        if (evaluator == null) {

            return false;
        }

        return evaluator.hasPermission(
                authentication.getName(), targetDomainObject, (String) permission);
    }

    @Override
    public boolean hasPermission(
            final Authentication authentication,
            final Serializable targetId,
            final String targetType,
            final Object permission) {

        final TypePermissionEvaluator evaluator = typePermissionEvaluators.get(permission);

        if (evaluator == null) {

            return false;
        }

        return evaluator.hasPermission(
                authentication.getName(), (String) targetId, targetType, (String) permission);
    }
}
