package com.networkprobe.core;

import java.lang.reflect.Method;

public final class MethodInfo {

    private final Method method;

    public MethodInfo(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
