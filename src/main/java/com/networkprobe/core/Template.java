package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.CommandEntity;
import com.networkprobe.core.domain.Command;
import com.networkprobe.core.domain.Networking;
import com.networkprobe.core.domain.Route;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.util.Key;

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
@Documented(done = false)
public interface Template {

    Networking getNetworking();
    void configureNetworking(Networking networking);
    Map<String, Route> getRoutes();
    Map<String, Command> getCommands();

    static String parameterized(ResponseEntity<?> responseEntity, List<String> arguments) {
        return responseEntity.getContent(arguments).toString();
    }

    default Command fromRequest(CommandRequest commandRequest) {
        return getCommands()
                .get(nonNull(commandRequest, "commandRequest")
                        .command());
    }

    default String getResponseByName(String commandName) {
        return getCommands()
                .get(commandName)
                .getResponse()
                /* unauthorizedResponse e unknownResponse não necessitam de parâmetros */
                .getContent(Collections.emptyList())
                .toString();
    }

    default void clearTemplateSchema() {
        getCommands().entrySet()
                .removeIf(commandEntry ->
                        commandEntry.getValue()
                        .getClass()
                        .isAnnotationPresent(CommandEntity.class));
        getRoutes().clear();
    }

    default String unauthorizedResponse() {
        return getResponseByName(Key.CMD_UNAUTHORIZED);
    }

    default String unknownResponse() {
        return getResponseByName(Key.CMD_UNKNOWN);
    }

}
