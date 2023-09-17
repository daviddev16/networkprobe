package com.networkprobe.core;


import com.networkprobe.Launcher;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.DependencyException;
import com.networkprobe.core.exception.SingletonException;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static com.networkprobe.core.util.Validator.*;

/**
 *
 *  Classe responsável por gerenciar instâncias dinâmicas e geradas através de criação de objeto padrão por
 * construtor. Os tipos dinâmicos em {@link SingletonType} são 'LAZY' e 'DYNAMIC', que não são objetos criados
 * através do construtor usando a keyword '{@code new ...()}'.
 *
 * Atualmente o SingletonDirectory faz mais do que classificar classes como Singleton. A classe gerência instâncias
 * não-singleton e injeção de dependências através de reflexões.
 *
 * @see SingletonType
 *
 */
public final class SingletonDirectory {

    private static final String BASE_SCAN_PACKAGE = "com.networkprobe";
    private static final Logger LOG = LoggerFactory.getLogger(SingletonDirectory.class);
    private static final Map<Class<?>, SingletonClassInfo> singletonInfoMap = new HashMap<>();

    private static final Comparator<Class<?>> INSTANCE_ORDER = (o1, o2) ->
    {
        Singleton singletonClass1 = o1.getAnnotation(Singleton.class);
        Singleton singletonClass2 = o2.getAnnotation(Singleton.class);
        return Integer.compare(singletonClass1.order(), singletonClass2.order());
    };

    public static void registerAllDeclaredSingletonClasses() throws InstantiationException, IllegalAccessException {

        Reflections reflections = new Reflections(BASE_SCAN_PACKAGE);

        List<Class<?>> singletonClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(Singleton.class));
        singletonClasses.sort(INSTANCE_ORDER);

        for (Class<?> dynamicSigletonClass : singletonClasses)
        {
            Singleton singleton = dynamicSigletonClass.getAnnotation(Singleton.class);
            if (singleton.creationType() == SingletonType.INSTANTIATED)
            {
                LOG.warn("Não é possível utilizar instância dinâmica em uma classe do tipo singleton " +
                        "'INSTANTIATED'. {} será ignorada.", dynamicSigletonClass.getSimpleName());
                return;
            }
            registerDynamicInstance(dynamicSigletonClass, singleton.creationType());
        }
        for (SingletonClassInfo singletonClassInfo : singletonInfoMap.values())
        {
            if (singletonClassInfo.getSingletonType() == SingletonType.DYNAMIC)
                internalFieldInjection(singletonClassInfo.getInstance());
        }
    }

    public static void registerDynamicInstance(Class<?> objectClass, Object instantiationObject,
                                               SingletonType singletonType)
            throws InstantiationException, IllegalAccessException {
        checkIsNotNull(objectClass, "objectClass");
        checkIsNotNull(singletonType, "singletonType");

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

    public static void registerCustomInstance(Object instantiationObject)
            throws InstantiationException, IllegalAccessException
    {
        registerDynamicInstance(instantiationObject.getClass(), instantiationObject, SingletonType.INSTANTIATED);
    }

    public static void registerDynamicInstance(Class<?> objectClass, SingletonType singletonType)
            throws InstantiationException, IllegalAccessException
    {
        registerDynamicInstance(objectClass, null, singletonType);
    }

    private static void internalFieldInjection(Object instantiationObject) {
        try {
            Class<?> objectClass = instantiationObject.getClass();
            for (Field dependencyField : objectClass.getDeclaredFields()) {

                if (!dependencyField.getDeclaringClass().isAssignableFrom(objectClass) ||
                        dependencyField.getAnnotation(ManagedDependency.class) == null)
                    continue;

                Object classifiedObject = getCompatibleInstanceOf(dependencyField.getType());
                internalPerformFieldInjection(instantiationObject, dependencyField, classifiedObject);
            }
        }
        catch (Exception e) {
            throw new DependencyException("Houve um problema na injeção de dependências.", e);
        }
    }

    private static void internalPerformFieldInjection(Object instantiationObject, Field dependencyField,
                                               Object valueObject) throws InvalidClassException, IllegalAccessException {
        checkIsNotNull(valueObject, "valueObject");

        Class<?> valueObjectType = valueObject.getClass();

        if (!dependencyField.getType().isAssignableFrom(valueObjectType))
            throw new InvalidClassException("Uma instância do tipo \"" + valueObjectType.getSimpleName() +
                    "\" não pode ser atribuida a uma variável do tipo \"" + valueObjectType.getSimpleName() + "\".");

        dependencyField.setAccessible(true);
        if (dependencyField.get(instantiationObject) == null)
            dependencyField.set(instantiationObject, valueObject);
    }

    @SuppressWarnings({"unchecked"})
    private static <E> E internalGenericSingleOf(Class<E> objectClass) throws InstantiationException, IllegalAccessException {

        SingletonClassInfo classInfo = singletonInfoMap.getOrDefault(objectClass, null);
        if (classInfo == null)
            throw new NullPointerException("Não há instância registada do tipo \"" + objectClass.getName() + "\".");

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

    @Nullable
    public static Object getCompatibleInstanceOf(Class<?> objectClass){
        try {
            SingletonClassInfo classInfo = singletonInfoMap.values()
                    .parallelStream()
                    .filter(singletonClassInfo -> objectClass
                            .isAssignableFrom(singletonClassInfo.getObjectClass()))
                    .findAny()
                    .orElseThrow(() -> new NullPointerException("Não foi possível encontrar " +
                            "uma classe compatível para \"" + objectClass.getName() + "\"."));

            return classInfo.getInstance();

        } catch (Exception e) {
            throw new SingletonException("Houve um erro ao recuperar uma instância singleton", e);
        }
    }

    @Nullable
    public static Object getBasedDependency(Class<?> basedClass) {
        return getCompatibleInstanceOf(basedClass);
    }

    /**
     * Responsável pelo comportamento de classe Singleton.
     **/
    public static void denyInstantiation(Class<?> objectClass) {
        if (containsInstanceInfo(objectClass))
            throw new SingletonException(objectClass, "tentativa de instância usando construtor não permitida");
    }

    public static void denyInstantiation(Object object) {
        denyInstantiation(checkIsNotNull(object, "object").getClass());
    }

    private static boolean containsInstanceInfo(Class<?> objectClass) {
        SingletonClassInfo classInfo = singletonInfoMap.get(objectClass);
        return classInfo != null && classInfo.getInstance() != null;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T getLazyInstanceInternal(SingletonClassInfo classInfo)
            throws InstantiationException, IllegalAccessException {

        Object objectInstance = classInfo.getInstance();
        if (objectInstance == null) {
            objectInstance = newDynamicInstanceInternal(classInfo.getObjectClass());
            internalFieldInjection(objectInstance);
        }

        return (T) objectInstance;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T newDynamicInstanceInternal(Class<?> objectClass)
            throws SingletonException, InstantiationException, IllegalAccessException {

        checkIsNotNull(objectClass, "objectClass");

        if (objectClass.getConstructors().length == 0)
            throw new SingletonException(objectClass, "Não há construtores nessa classe");

        Constructor<?> constructors = objectClass.getConstructors()[0];

        if (constructors.getParameterCount() > 0)
            throw new SingletonException(objectClass, "Não é possível utilizar instânciar um construtor com parâmetros");

        return (T) objectClass.newInstance();
    }

    private static void addToInfoMap(Class<?> objectClass, SingletonClassInfo classInfo) {
        if (singletonInfoMap.put(objectClass, classInfo) == null) {
            LOG.info("A classe '{}' foi registrada como singleton do tipo '{}'.",
                    objectClass.getName(), classInfo.getSingletonType());
            return;
        }
        LOG.error("Houve um erro na inserção da classe '{}' no mapa de singletons.", objectClass.getName());
    }

}
