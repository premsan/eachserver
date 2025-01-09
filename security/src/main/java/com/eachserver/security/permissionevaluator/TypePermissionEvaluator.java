package com.eachserver.security.permissionevaluator;

public interface TypePermissionEvaluator {

    String evaluates();

    boolean hasPermission(
            final String principalName,
            final String targetId,
            final String targetType,
            final String permission);
}
