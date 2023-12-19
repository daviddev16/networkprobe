package com.networkprobe.core.annotation.reflections;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.entity.caching.ProcessedResponseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassInventory é usado para marcar uma classe como um inventário de métodos que
 * serão usados nos comandos do template para execução dinâmica. Métodos de inventário
 * são executados em tempo de execução e retornam valores processados na entidade para
 * o usuário.
 *
 * @see ProcessedResponseEntity
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ClassInventory { }
