package com.networkprobe.core.annotation.miscs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code Documented} marca as classes que já foram documentadas. Usado junto ao IntelliJ
 * para mapear as classes que não foram documentadas.
 **/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Documented {

    /**
     * O valor "done" define se a classe já foi documentada ou não.
     * */
    boolean done() default true;

}
