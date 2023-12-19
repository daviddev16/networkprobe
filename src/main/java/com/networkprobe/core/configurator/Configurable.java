package com.networkprobe.core.configurator;

import com.networkprobe.core.annotation.miscs.*;
import com.networkprobe.core.annotation.reflections.*;
import com.networkprobe.core.SingletonDirectory;


/**
 * Configurable é utilizado para executar alguma configuração arbitrária sem precisar
 * instânciar e chamar o méotodo {@link Configurable#configure()} manualmente. Apesar
 * da configuração ser dinâmica, ainda é necessário utilizar a anotação {@link Singleton}
 * para que o objeto Java seja instânciado e gerenciador pelo {@link SingletonDirectory}.
 *
 * @see SingletonDirectory
 **/
@Documented
public interface Configurable {

    /**
     * Configure executa a configuração implementada dentro do método. A ordem de execução
     * é definida pela anotação {@link Singleton}.
     *
     * @throws Exception Caso ocorra algum erro na execução da tarefa.
     * */
    void configure() throws Exception;

}
