package com.networkprobe.core.entity;

import com.networkprobe.core.caching.*;

import java.util.List;

/**
 * ResponseEntity é uma interface que viabiliza as respostas das requisições
 * dos comandos recebidos, podendo ser um conteúdo estático ou dinâmico determinado
 * pela implementação da interface. Veja as implementações abaixo:
 *
 * @see CachedResponseEntity
 * @see ProcessedResponseEntity
 * @see StaticResponseEntity
 *
 * */
public interface ResponseEntity<T> {

    /**
     * getRawContent retorna o valor original da resposta que for configurada
     * dentro do comando no template.
     * */
    String getRawContent();

    /**
     * getContent retorna um valor de resposta determinado pela implementação
     * da interface, podendo ser um conteúdo estático ou dinâmico.
     * */
    T getContent(List<String> arguments);

    /**
     * isCachedOnce será utilizado para determinar se a response vai precisar
     * ser re-processada ou o conteúdo processado pela primeira vez, será
     * utilizado novamente nas próximas vezes. Esse método irá variar de acordo
     * com a implementação.
     *
     * @see ProcessedResponseEntity
     * @see CachedResponseEntity
     *
     * */
    boolean isCachedOnce();

}
