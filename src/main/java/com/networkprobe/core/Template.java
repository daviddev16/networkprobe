package com.networkprobe.core;

import com.networkprobe.core.model.Command;
import com.networkprobe.core.model.Key;
import com.networkprobe.core.model.Networking;
import com.networkprobe.core.model.Route;
import com.networkprobe.core.entity.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.networkprobe.core.util.Validator.nonNull;

/**
 *
 * Um template representa um estado de configuração que será utilizado para enviar respostas estáticas ou dinâmicas
 * para um cliente TCP/IP de acordo do que for requisitado. As requisições são definidas pelo nome do comando que será
 * enviado pelo canal.
 *
 * */
public interface Template {

    static String parameterized(ResponseEntity<?> responseEntity, List<String> arguments) {
        return responseEntity.getContent(arguments).toString();
    }

    default Command fromRequest(CommandRequest commandRequest) {
        return getCommands()
                .get(nonNull(commandRequest, "commandRequest")
                .command());
    }

    default String queryResponse(String key) {
        return getCommands()
                .get(key)
                .getResponse()
                /* unauthorizedResponse e unknownResponse não necessitam de parâmetros */
                .getContent(Collections.emptyList())
                .toString();
    }

    default String unauthorizedResponse() {
        return queryResponse(Key.CMD_UNAUTHORIZED);
    }

    default String unknownResponse() {
        return queryResponse(Key.CMD_UNKNOWN);
    }

    Networking getNetworking();
    Map<String, Route> getRoutes();
    Map<String, Command> getCommands();

}
