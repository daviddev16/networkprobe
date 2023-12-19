package com.networkprobe.core.annotation.reflections;

import com.networkprobe.core.annotation.miscs.Documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.networkprobe.core.SingletonDirectory;

/**
 * CommandEntity é reponsável por determinar quais classes são um comando interno da
 * aplicação e devem ser instânciadas pelo {@link SingletonDirectory} e registrada no
 * Template atual.
 *
 * @see SingletonDirectory
 *
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandEntity {

    String commandName();

    boolean enabled() default true;

}
