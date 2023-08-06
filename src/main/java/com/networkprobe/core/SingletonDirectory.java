package com.networkprobe.core;


import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.SingletonException;
import com.networkprobe.core.util.Validator;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 *
 *  Classe responsável por gerenciar instâncias dinâmicas e geradas através de criação de objeto padrão por
 * construtor. Os tipos dinâmicos em {@link SingletonType} são 'LAZY' e 'DYNAMIC', que não são objetos criados
 * através do construtor usando a keyword '{@code new ...()}'.
 *
 * @see SingletonType
 *
 */
public final class SingletonDirectory {

    private static final String BASE_SCAN_PACKAGE = "com.networkprobe";
    private static final Logger LOG = LoggerFactory.getLogger(SingletonDirectory.class);
    private static final Map<Class<?>, SingletonClassInfo> singletonInfoMap = Collections.synchronizedMap(new HashMap<>());
    private static final Comparator<Class<?>> EXECUTION_ORDER = (o1, o2) ->
    {
        Singleton singletonClass1 = o1.getAnnotation(Singleton.class);
        Singleton singletonClass2 = o2.getAnnotation(Singleton.class);
        return Integer.compare(singletonClass1.order(), singletonClass2.order());
    };

    public static void registerAllDeclaredSingletonClasses() throws InstantiationException, IllegalAccessException {

        Reflections reflections = new Reflections(BASE_SCAN_PACKAGE);

        List<Class<?>> singletonClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(Singleton.class));
        singletonClasses.sort(EXECUTION_ORDER);

        for (Class<?> dynamicSigletonClass : singletonClasses) {
            Singleton singleton = dynamicSigletonClass.getAnnotation(Singleton.class);
            if (singleton.creationType() == SingletonType.INSTANTIATED)
            {
                LOG.warn("Não é possível utilizar instância dinâmica em uma classe do tipo singleton " +
                        "'INSTANTIATED'. {} será ignorada.", dynamicSigletonClass.getSimpleName());
                return;
            }
            registerDynamicInstance(dynamicSigletonClass, singleton.creationType());
        }

    }

    public static void registerDynamicInstance(Class<?> objectClass, Object instantiationObject,
                                               SingletonType singletonType) throws InstantiationException, IllegalAccessException {

        Validator.checkIsNotNull(objectClass, "objectClass");
        Validator.checkIsNotNull(singletonType, "singletonType");

        if (containsInstanceInfo(objectClass))
            throw new SingletonException(objectClass, "essa classe já foi registrada no mapa de singletons");

        if (singletonType == SingletonType.INSTANTIATED)
        {
            if (instantiationObject == null)
                throw new SingletonException(objectClass, "Uma classe não pode ser registrada com o tipo 'INSTANTIATED' sem" +
                        " ter um objeto já instânciado pasado no parâmetro 'instantiationObject'");
            else
                addToInfoMap(objectClass, new SingletonClassInfo(objectClass, instantiationObject, singletonType));
        }
        else if (singletonType == SingletonType.LAZY)
        {
            if (instantiationObject != null)
                throw new SingletonException(objectClass, "Ao utilizar o tipo de instância 'LAZY', não é aceito a tentativa" +
                        "de instância manualmente passada no parâmetro 'instantiationObject', favor alterar para 'null'");
            else
                addToInfoMap(objectClass, new SingletonClassInfo(objectClass, null, singletonType));
        }
        else if (singletonType == SingletonType.DYNAMIC)
        {
            Object dynamicObjectInstance = (instantiationObject != null) ? instantiationObject : newDynamicInstanceInternal(objectClass);
            addToInfoMap(objectClass, new SingletonClassInfo(objectClass, dynamicObjectInstance, singletonType));
        }

    }

    public static void registerDynamicInstance(Class<?> objectClass, SingletonType singletonType)
            throws InstantiationException, IllegalAccessException
    {
        registerDynamicInstance(objectClass, null, singletonType);
    }

    @SuppressWarnings({"unchecked"})
    private static <E> E internalGenericSingleOf(Class<E> objectClass) throws InstantiationException, IllegalAccessException {

        SingletonClassInfo classInfo = singletonInfoMap.getOrDefault(objectClass, null);
        if (classInfo == null) return null;

        /* não deveria retornar nulo já que é um instância registrada diretamente no 'registerDynamicInstance'. */
        if (classInfo.getSingletonType() == SingletonType.INSTANTIATED || classInfo.getSingletonType() == SingletonType.DYNAMIC)
            return (E) classInfo.getInstance();

        else if (classInfo.getSingletonType() == SingletonType.LAZY)
            return getLazyInstanceInternal(classInfo);

        return null;
    }

    @Nullable
    public static <E> E getSingleOf(Class<E> objectClass){
        try {
            return internalGenericSingleOf(objectClass);
        } catch (Exception e) {
            throw new SingletonException("Houve um erro ao recuperar uma instância singleton", e);
        }
    }

    public static void denyInstantiation(Class<?> objectClass) {
        Validator.checkIsNotNull(objectClass, "objectClass");
        if (containsInstanceInfo(objectClass))
            throw new SingletonException(objectClass, "tentativa de instância usando construtor não permitida");
    }

    public static void denyInstantiation(Object object) {
        denyInstantiation(Validator.checkIsNotNull(object.getClass(), "object"));
    }

    private static boolean containsInstanceInfo(Class<?> objectClass) {
        SingletonClassInfo classInfo = singletonInfoMap.getOrDefault(objectClass, null);
        return classInfo != null && classInfo.getInstance() != null;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T getLazyInstanceInternal(SingletonClassInfo classInfo)
            throws InstantiationException, IllegalAccessException {

        Object objectInstance = classInfo.getInstance();

        if (objectInstance == null)
            objectInstance = newDynamicInstanceInternal(classInfo.getObjectClass());

        return (T) objectInstance;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T newDynamicInstanceInternal(Class<?> objectClass)
            throws SingletonException, InstantiationException, IllegalAccessException {

        Validator.checkIsNotNull(objectClass, "objectClass");

        if (objectClass.getConstructors().length == 0)
            throw new SingletonException(objectClass, "Não há construtores nessa classe");

        Constructor<?> constructors = objectClass.getConstructors()[0];

        if (constructors.getParameterCount() > 0)
            throw new SingletonException(objectClass, "Não é possível utilizar instânciar um construtor com parâmetros");

        return (T) objectClass.newInstance();
    }

    private static void addToInfoMap(Class<?> objectClass, SingletonClassInfo classInfo) {
        if (singletonInfoMap.put(objectClass, classInfo) != null) {
            LOG.info("A classe '{}' foi registrada como singleton do tipo '{}'.",
                    objectClass.getName(), classInfo.getSingletonType());
            return;
        }
        LOG.error("Houve um erro na inserção da classe '{}' no mapa de singletons.", objectClass.getName());
    }
}