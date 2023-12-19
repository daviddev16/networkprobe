package com.networkprobe.core.annotation.miscs;

/**
 * Feature é utilizado quando uma nova ferramenta ou design está sendo implementado,
 * porém, ainda está em desenvolvimento e teste.
 **/
@Documented
public @interface Feature {

    boolean notImplemented() default true;

}
