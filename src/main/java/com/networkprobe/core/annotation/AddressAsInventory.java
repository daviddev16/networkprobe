package com.networkprobe.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AddressAsInventory é usado para marcar uma classe como um inventário de métodos que
 * serão usados nos comandos do template para execução dinâmica.
 *
 * @see com.networkprobe.core.caching.ProcessedResponseEntity
 *
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AddressAsInventory { }
