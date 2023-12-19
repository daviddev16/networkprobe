package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.entity.caching.CachedResponseEntity;
import com.networkprobe.core.entity.caching.ProcessedResponseEntity;
import com.networkprobe.core.domain.Command;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.entity.base.StaticTextResponseEntity;

/**
 * ResponseEntityFactory será utilizado para definir qual será a implementação
 * do ResponseEntity de acordo com o conteúdo do campo 'response' de um comando.
 *
 * @see Command
 *
 * */
@Documented
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
     * @see StaticTextResponseEntity
     *
     * */
    ResponseEntity<?> responseEntityOf(String rawContent, boolean cachedOnce);

}
