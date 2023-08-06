package com.networkprobe.core;

public final class SingletonClassInfo {

    private final Object instance;
    private final Class<?> objectClass;
    private final SingletonType singletonType;

    public SingletonClassInfo(Class<?> objectClass, Object instance, SingletonType singletonType) {
        this.objectClass = objectClass;
        this.instance = instance;
        this.singletonType = singletonType;
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public SingletonType getSingletonType() {
        return singletonType;
    }

}