package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import org.slf4j.Logger;

@Documented(done = false)
public final class ExceptionHandler {

    /**
     * handleUnexpected é usado para derrubar a aplicação quando uma exceção em tempo real
     * ocorre indevidamente. A classe {@link Reason} mapea os casos onde pode ocorrer e informa
     * dica e exitCode para a finalização da aplicação de forma controlada e informativa.
     *
     * @param logger O objeto de log da classe que está lançando a exceção.
     * @param exception A exceção lançada indevidamente.
     * @param reason A razão por trás da exceção ter lançado.
     *
     * */
    public static void handleUnexpected(Logger logger, Exception exception, Reason reason) {
        logger.error("Uma exceção não esperada foi lançanda, encerrando serviço com estado: {}", reason.getDisplayName());
        logger.error(reason.getHint());
        logger.error("Trace: ", exception);
        Runtime.getRuntime().exit(reason.getExitCode());
    }

}
