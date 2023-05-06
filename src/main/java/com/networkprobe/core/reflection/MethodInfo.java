package com.networkprobe.core.reflection;

import java.lang.reflect.Method;
import java.util.List;

public final class MethodInfo {

    private final Method method;

    public MethodInfo(Method method) {
        this.method = method;
    }

    public boolean matches(List<Object> arguments) {
        return false;
    }

    public Method getMethod() {
        return method;
    }
}
