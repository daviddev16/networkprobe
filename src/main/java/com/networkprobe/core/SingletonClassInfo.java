package com.networkprobe.core;

public final class SingletonClassInfo {

    private Object instance;
    private Class<?> objectClass;
    private SingletonType singletonType;

    public SingletonClassInfo(Class<?> objectClass, Object instance, SingletonType singletonType) {
        this.objectClass = objectClass;
        this.instance = instance;
        this.singletonType = singletonType;
    }

    protected void updateInstance(Object instance) {
        this.instance = instance;
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