package com.networkprobe.core;

import com.networkprobe.core.JsonTemplateAdapter;
import com.networkprobe.core.config.Command;
import com.networkprobe.core.config.Key;
import com.networkprobe.core.config.Networking;
import com.networkprobe.core.config.Route;

import java.util.Map;

/**
 *
 * Um template representa um estado de configuração que será utilizado para enviar respostas estáticas ou dinâmicas
 * para um cliente TCP/IP de acordo do que for requisitado. As requisições são definidas pelo nome do comando que será
 * enviado pelo canal.
 *
 * */
public interface Template {

    /**
     * Retorna os valores de configuração de rede do Template
     * */
    Networking getNetworking();

    /**
     * Retorna um Map contendo as rotas do Template
     * */
    Map<String, Route> getRoutes();

    /**
     * Retorna um Map contendo as informações dos comandos do Template
     * */
    Map<String, Command> getCommands();

    default String unauthorizedResponse() {
        return defaultInternalResponse(Key.CMD_UNAUTHORIZED);
    }

    default String unknownResponse() {
        return defaultInternalResponse(Key.CMD_UNKNOWN);
    }

    default String defaultInternalResponse(String key) {
        return (String) getCommands()
                .get(key)
                .getResponse()
                .getContent();
    }

}
