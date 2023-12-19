package com.networkprobe.core.annotation.reflections;

import com.networkprobe.core.SingletonDirectory;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.miscs.Documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A anotação Singleton é responsável por determinar quais classes serão instânciadas dinâmicamente ou
 * através do tipo 'SingletonType.LAZY' que são instânciadas quando são necessárias e 'SingletonType.DYNAIC'
 * que são instanciadas na execução do programa ordenadamente.
 *
 * @see SingletonDirectory
 *
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Singleton {

    /**
    * Define como a classe anotada será instânciada
    * */
    SingletonType creationType();

    /**
     * Define a ordem em que a classe será instânciada pelo SingletonDirectory.
     *
     * @see SingletonDirectory
     * */
    int order() default 0;

    /**
     * Informa ao SingletonDirectory se essa classe será instanciada ou foi
     * desativada de ser gerenciada pelo mesmo.
     **/
    boolean enabled() default true;

}
