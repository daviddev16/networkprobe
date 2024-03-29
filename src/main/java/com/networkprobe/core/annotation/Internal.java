package com.networkprobe.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal é usado para marcar um método como um método de funcionalidade interna e que não sera
 * acessado/executado por um comando dentro de um template.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Internal { }
