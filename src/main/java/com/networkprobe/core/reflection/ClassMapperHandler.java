package com.networkprobe.core.reflection;

import com.networkprobe.core.annotation.Addressable;
import com.networkprobe.core.annotation.NotAllowed;

import javax.management.InstanceAlreadyExistsException;
import java.lang.reflect.Method;
import java.util.*;

import static com.networkprobe.core.util.Validator.checkIsNotNull;

public final class ClassMapperHandler {

    private final Map<String, MethodInfo> methods = Collections.synchronizedMap(new HashMap<>());
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public void extract(Object instance) throws InstanceAlreadyExistsException, IllegalAccessException {
        checkIsNotNull(instance, "instance");

        final Class<?> clazz = instance.getClass();

        if (instances.containsKey(clazz))
            throw new InstanceAlreadyExistsException(String.format("Uma instância de %s já foi registrada.", clazz));
        else if (!isClassAddressable(clazz))
            throw new IllegalAccessException(String.format("Não é possível endereçar a classe \"%s\" por que não " +
                    "contém a anotação de endereçamento \"%s\".", clazz.getName(), Addressable.class.getName()));

        instances.put(clazz, instance);
        extractAllMethods(clazz);
    }

    public void extract(Class<?> clazz) throws InstantiationException, IllegalAccessException,
            InstanceAlreadyExistsException {
        checkIsNotNull(clazz, "clazz");
        extract(clazz.newInstance());
    }

    private void extractAllMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (isMethodAllowed(method))
                getMethods().put(method.getName(), new MethodInfo(method));
        }
    }

    public boolean isClassAddressable(Class<?> clazz) {
        return clazz != null && clazz.getAnnotation(Addressable.class) != null;
    }

    public boolean isMethodAllowed(Method method) {
        return method != null && method.getAnnotation(NotAllowed.class) != null;
    }

    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    public Map<Class<?>, Object> getInstances() {
        return instances;
    }
}
