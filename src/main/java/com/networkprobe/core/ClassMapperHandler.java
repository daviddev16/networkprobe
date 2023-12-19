package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.ClassInventory;
import com.networkprobe.core.annotation.reflections.Data;
import com.networkprobe.core.annotation.reflections.Internal;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.exception.ExecutionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.networkprobe.core.util.Validator.*;
import static java.lang.String.format;

/**
 * ClassMapperHandler mapea todos os métodos que serão utilizados para invocação
 * de valores dinâmicos dentro do ProcessedResponseEntity, permitindo que valores
 * não estaticos sejam gerados como resposta e armazenados em cache temporário.
 **/
@Singleton(creationType = SingletonType.DYNAMIC, order = -1000)
@Documented(done = false)
public final class ClassMapperHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMapperHandler.class);
    private final Map<Class<?>, Object> instancesMap = new HashMap<>();
    private final Map<String, Method> methodsMap = new HashMap<>();
    private static ClassMapperHandler classMapperInstance;

    public static final String INVALID_METHOD = "<<invalid_method>>";
    public static final String NONE = "<<?>>";


    public ClassMapperHandler() {
        SingletonDirectory.denyInstantiation(this);
    }

    public void extract(Object instance)
            throws InstanceAlreadyExistsException, IllegalAccessException {

        Class<?> clazz = nonNull(instance, "instance").getClass();

        if (instancesMap.containsKey(clazz))
            throw new InstanceAlreadyExistsException(format("Uma instância de %s já foi registrada.", clazz));

        else if (!isClassAddressable(clazz))
            throw new IllegalAccessException(format("Não é possível endereçar a classe \"%s\" por que não " +
                    "contém a anotação de endereçamento \"%s\".", clazz.getName(), ClassInventory.class.getName()));

        instancesMap.put(clazz, instance);
        extractAllMethods(clazz);
    }

    private void extractAllMethods(Class<?> clazz)
    {
        nonNull(clazz, "clazz");
        for (Method method : clazz.getDeclaredMethods()) {

            if (method.getAnnotation(Internal.class) != null)
                continue;

            Data useAsDataAnn = method.getAnnotation(Data.class);
            getMethods().put((useAsDataAnn != null) ?
                    useAsDataAnn.name() : method.getName(), method);
        }
    }

    /**
     * O método execute(...) faz a execução do método informando no parâmetro "methodName", caso
     * exista no mapa de métodos extraidos da ClassMapperHandler.
     *
     * @param methodName O nome do método mapeado usando a anotação {@link Data#name()}.
     *                   Caso não tenha a anotação informada, o nome do método será usado
     *
     * @param arguments Os valores em String passados no template para a execução do
     *                  método. Antes de ser processo o método, cada argumento é
     *                  transformado para seus respectivos tipos. Um inteiro na String,
     *                  vai virar um Integer internamente.
     *
     * @return Caso a invocação do método interno retorne null, o
     *         método execute retonará o valor {@code ClassMapperHandler.NONE}.
     *         Caso a invocação não falhe e não retorn null, o método execute
     *         retornará o valor processado dentro da função interna com sucesso.
     *
     * @exception ExecutionFailedException Caso haja algum erro na invocação do método
     *                                     interno. Essa exception derruba a aplicação
     *                                      com status {@code Reason.NPS_CLASS_MAPPER_PROCESS}.
     *
     * @see Data
     * @see ClassInventory
     **/
    public String execute(String methodName, List<String> arguments) {
        nonNull(arguments, "arguments");
        nonNull(methodName, "methodName");
        Method method = null;
        try {
            method = methodsMap.get(methodName);
            nonNullWithMessage(method, format("O método '%s' não foi encontrado.", methodName));
            Object value = invokeMethod(method, convertArgumentsToTypes(method, arguments));
            return value == null ? NONE : value.toString();
        }
        catch (Exception exception) {

            String message = "";
            if (exception instanceof InvocationTargetException)
                message = format(
                        "Houve um erro interno na execução da função \"%s\". Verifique os " +
                        "argumentos da função no arquivo de configuração. [%s]", methodName,
                        exception.getCause().getMessage()
                );

            else if (exception instanceof IllegalAccessException)
                message = format("A função \"%s\" é inacessível.", methodName);

            else if (exception instanceof IllegalArgumentException)
                message = format("Os argumentos informados na função \"%s\" são inválidos. Ordenação de " +
                                "tipos correta: %s. Verifique os argumentos da função no arquivo de configuração.",
                        methodName, convertParametersToStrings(method));
            else
                message = exception.getMessage();

            /*
              A aplicação não deve continuar processando entidades onde seus métodos são inválidos ou lançam
              exceção. Sendo assim, é utilizado o handleUnexpected para derrubar a aplicação para que seja feito
              a correção no arquivo template.
            */
            ExceptionHandler.handleUnexpected(LOG, new ExecutionFailedException(message),
                    Reason.NPS_CLASS_MAPPER_PROCESS);
        }
        return null;
    }

    private Object invokeMethod(Method method, List<Object> args) throws InvocationTargetException, IllegalAccessException {
        nonNull(method, "method");
        Class<?> methodType = method.getDeclaringClass();
        Object instanceOfMethod = getInstances().get(methodType);
        Object[] arguments = args.toArray(new Object[args.size()]);
        method.setAccessible(true);
        return method.invoke(instanceOfMethod, arguments);
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
                clazz.getAnnotation(ClassInventory.class) != null;
    }



    public Map<String, Method> getMethods() {
        return methodsMap;
    }

    public Map<Class<?>, Object> getInstances() {
        return instancesMap;
    }

    public static ClassMapperHandler getInstance() {
        return (classMapperInstance != null) ? classMapperInstance :
                (classMapperInstance = SingletonDirectory.getSingleOf(ClassMapperHandler.class));
    }
}
