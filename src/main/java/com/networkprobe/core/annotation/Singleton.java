package com.networkprobe.core.annotation;

import com.networkprobe.core.SingletonType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A anotação Singleton é responsável por determinar quais classes serão instânciadas dinâmicamente ou
 * através do tipo 'SingletonType.LAZY' que são instânciadas quando são necessárias.
 *
 * @see com.networkprobe.core.SingletonDirectory
 *
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {

    /**
    * Define como a classe anotada será instânciada
    * */
    SingletonType creationType();

    /**
     * Define a ordem em que a classe será instânciada pelo SingletonDirectory.
     *
     * @see com.networkprobe.core.SingletonDirectory
     * */
    int order() default 0;

    boolean enabled() default true;

}
