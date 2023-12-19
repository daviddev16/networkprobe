package com.networkprobe.core.annotation.reflections;

import com.networkprobe.core.annotation.miscs.Documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A anotação Handled marca uma variável como uma variável manipulada por injeção de dependência.
 * A injeção de dependência ocorre quando a classe é instanciada pelo SingletonDirectory, que gerência
 * a instância da classe e as dependências da mesma.
 **/
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handled { }
