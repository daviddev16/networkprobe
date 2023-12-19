package com.networkprobe.core.annotation.reflections;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.ClassMapperHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data é usado para marcar um método que será usado em classes marcada como{@link ClassInventory},
 * podendo alterar o nome do método dentro do arquivo de template.
 *
 * @see ClassInventory
 *
 **/
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {

    /**
     * Nome da função que será executada dinâmicamente. Caso seja vazio ou nulo,a aplicação
     * utilizara o nome do método em java..
     *
     * @see ClassMapperHandler
     **/
    String name() default "";

}
