package com.eachserver.security.permissionevaluator;

public interface ObjectPermissionEvaluator<T> {

    Class<T> evaluates();

    boolean hasPermission(
            final String principalName, final T targetObject, final String permission);
}
