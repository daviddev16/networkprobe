package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.entity.ResponseEntity;
import com.networkprobe.core.caching.ProcessedResponseEntity;
import com.networkprobe.core.entity.StaticResponseEntity;
import com.networkprobe.core.experimental.Simplexer;

/**
 *  CommandResponseFactory é uma implementação do ResponseEntityFactory, que  é responsável
 *  por determinar qual será a o tipo de ResponseEntity conforme o conteúdo do comando informado
 *  no arquivo de template JSON. O CommandResponseFactory faz a verificação se há expressão processável
 *  dentro do comando e faz decisão se o comando vai ser do tipo processado ou um conteúdo estático
 *  como uma String, por exemplo.
 **/

@Singleton(creationType = SingletonType.DYNAMIC, order = 10)
public final class CommandResponseFactory implements ResponseEntityFactory {

    public CommandResponseFactory()
    {
        SingletonDirectory.denyInstantiation(this);
    }

    public ResponseEntity<?> responseEntityOf(String rawContent, boolean cachedOnce)
    {
        if (Simplexer.containsExpression(rawContent))
            return new ProcessedResponseEntity(rawContent, cachedOnce);
        else
            return new StaticResponseEntity(rawContent);
    }

    public static CommandResponseFactory getFactory() {
        return SingletonDirectory.getSingleOf(CommandResponseFactory.class);
    }

}
