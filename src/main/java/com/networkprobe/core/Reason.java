package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public enum Reason {

    TCP_SOCKET_BEGIN           (900, "Serviço finalizado quando for lançado uma exceção durante a construção/início do soquete TCP."),
    TCP_SOCKET_UPDATE          (901, "Serviço finalizado quando for lançado uma exceção durante o loop de execução do serviço de troca de mensagens TCP"),
    TCP_CLIENT_HANDLER_PROCESS (902, "Serviço finalizado quando houver algum erro no processamento de informações do ClienteHandler."),

    UDP_SOCKET_BEGIN           (800, "Serviço finalizado quando for lançado uma exceção durante a construção/início do soquete UDP."),
    UDP_SOCKET_UPDATE          (801, "Serviço finalizado quando for lançado uma exceção durante o recebimento de flags de resposta UDP."),

    NPS_NETWORK_MONITOR_UPDATE (500, "Encerramento devido a problemas na rotina de execução de monitoramento."),

    NPS_CLASS_MAPPER_PROCESS   (200, "Encerramento devido a erro causado na execução de uma função dinâmica dentro de um ResponseEntity."),

    NPS_CONFIGURATOR_EXCEPTION (1000, "Ocorreu um erro durante a configuração do sistema."),
    NPS_FILE_LOADER_EXCEPTION  (1001, "Ocorreu um erro durante a leitura do arquivo de template.");

    private final int exitCode;
    private final String hint;

    Reason(int exitCode, String hint) {
        this.exitCode = exitCode;
        this.hint = hint;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getHint() {
        return "HINT: ".concat(hint);
    }

    public String getDisplayName() {
        return "ERROR_".concat(name().concat("_FAIL"));
    }

}
