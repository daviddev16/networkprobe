package com.networkprobe.core.reflection;

import com.networkprobe.core.annotation.Address;
import com.networkprobe.core.annotation.Hide;
import com.networkprobe.core.exception.ExecutionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.networkprobe.core.util.Utility.*;
import static com.networkprobe.core.util.Validator.checkIsNotNull;
import static com.networkprobe.core.util.Validator.checkIsNullOrEmpty;
import static java.lang.String.*;

public final class ClassMapperHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassMapperHandler.class);

    public static final String NONE = "<<?>>";

    private final Map<String, MethodInfo> methods = Collections.synchronizedMap(new HashMap<>());
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public void extract(Object instance)
            throws InstanceAlreadyExistsException, IllegalAccessException {

        checkIsNotNull(instance, "instance");
        Class<?> clazz = instance.getClass();

        if (getInstances().containsKey(clazz))
            throw new InstanceAlreadyExistsException(format("Uma instância de %s já foi registrada.", clazz));

        else if (!isClassAddressable(clazz))
            throw new IllegalAccessException(format("Não é possível endereçar a classe \"%s\" por que não " +
                    "contém a anotação de endereçamento \"%s\".", clazz.getName(), Address.class.getName()));

        getInstances().put(clazz, instance);
        extractAllMethods(clazz);
    }

    private void extractAllMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (isMethodAllowed(method))
                getMethods().put(method.getName(), new MethodInfo(method));
        }
    }

    public String execute(String methodName, List<String> arguments) throws ExecutionFailedException {
        checkIsNotNull(methodName, "methodName");
        checkIsNotNull(arguments, "arguments");

        MethodInfo methodInfo = getMethods().get(methodName);
        try {
            if (methodInfo == null)
                throw new NullPointerException(format("O método \"%s\" não existe.", methodName));

            Method originalMethod = methodInfo.getMethod();
            Object value = invokeMethod(originalMethod, convertArgumentsToTypes(originalMethod, arguments));
            return value == null ? NONE : value.toString();
        } catch (Exception e) {
            handleMethodInvocationExceptions(methodInfo, e);
        }
        return null;
    }

    private void handleMethodInvocationExceptions(MethodInfo methodInfo,
                                                        Exception exception) throws ExecutionFailedException {
        String message = "";

        if (exception instanceof InvocationTargetException)
            message = format("Houve um erro interno na execução da função \"%s\". Verifique os " +
                    "argumentos da função no arquivo de configuração.", methodInfo.getMethod().getName());

        else if (exception instanceof IllegalAccessException)
            message = format("A função \"%s\" é inacessível.", methodInfo.getMethod().getName());

        else if (exception instanceof IllegalArgumentException)
            message = String.format("Os argumentos informados na função \"%s\" são inválidos. " +
                            "Ordenação de tipos correta: %s. Verifique os argumentos da função no arquivo de configuração.",
                    methodInfo.getMethod().getName(), convertParametersToStrings(methodInfo.getMethod()));

        else
            message = String.format("Houve um erro inesperado na execução da " +
                    "função \"%s\".", methodInfo.getMethod().getName());

        throw new ExecutionFailedException(message + " " + exceptionShortDescription(exception));
    }

    private Object invokeMethod(Method method, List<Object> args)
            throws InvocationTargetException, IllegalAccessException {
        checkIsNotNull(method, "method");

        Class<?> methodType = method.getDeclaringClass();
        Object instanceOfMethod = getInstances().get(methodType);
        Object[] arguments = args.toArray(new Object[args.size()]);

        method.setAccessible(true);
        Object result = method.invoke(instanceOfMethod, arguments);
        method.setAccessible(false);

        return result;
    }

    private List<Object> convertArgumentsToTypes(Method method, List<String> arguments) {
        List<Object> objects = new ArrayList<>();
        Class<?>[] parametersTypes = method.getParameterTypes();

        if (parametersTypes.length != arguments.size())
            throw new RuntimeException("O número de argumentos é maior que o números" +
                    " de parâmetros da function " + method.getName());

        for (int i = 0; i < parametersTypes.length; i++) {
            Class<?> parameterType = parametersTypes[i];
            String argumentValue = arguments.get(i);
            objects.add(convertStringToType(parameterType, argumentValue));
        }

        return objects;
    }

    private Object convertStringToType(Class<?> clazz, String value) {
        checkIsNullOrEmpty(value, "value");

        if(Boolean.class == clazz || boolean.class == clazz)
            return Boolean.parseBoolean(value);
        else if(Byte.class == clazz || byte.class == clazz)
            return Byte.parseByte(value);
        else if(Short.class == clazz || short.class == clazz)
            return Short.parseShort(value);
        else if(Integer.class == clazz || int.class == clazz)
            return Integer.parseInt(value);
        else if(Long.class == clazz || long.class == clazz)
            return Long.parseLong(value);
        else if(Float.class == clazz || float.class == clazz)
            return Float.parseFloat(value);
        else if(Double.class == clazz || double.class == clazz)
            return Double.parseDouble(value);

        return value;
    }

    private String convertParametersToStrings(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringJoiner joiner = new StringJoiner(",");

        Arrays.stream(parameterTypes).forEach(parameterType ->
                joiner.add(parameterType.getTypeName()));

        return "[" + joiner + "]";
    }

    public boolean isClassAddressable(Class<?> clazz) {
        return clazz != null &&
                clazz.getAnnotation(Address.class) != null;
    }

    public boolean isMethodAllowed(Method method) {
        return method != null &&
                method.getAnnotation(Hide.class) == null;
    }

    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    public Map<Class<?>, Object> getInstances() {
        return instances;
    }
}
