package com.networkprobe.core;

import com.networkprobe.core.caching.*;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.entity.base.StaticResponseEntity;

/**
 * ResponseEntityFactory será utilizado para definir qual será a implementação
 * do ResponseEntity de acordo com o conteúdo do campo 'response' de um comando.
 *
 * @see com.networkprobe.core.model.Command
 *
 * */
public interface ResponseEntityFactory {

    /**
     * responseEntityOf faz a decisão de qual {@link ResponseEntity} será utilizado de
     * acordo com a String passada no parâmetro 'rawContent'.
     *
     * @param rawContent O conteúdo original do campo 'response' no comando.
     * @param cachedOnce Se o comando é armazenado em cache ou será processado mais de uma vez
     *                   se for dinâmico.
     *
     * @see CachedResponseEntity
     * @see ProcessedResponseEntity
     * @see StaticResponseEntity
     *
     * */
    ResponseEntity<?> responseEntityOf(String rawContent, boolean cachedOnce);

}
