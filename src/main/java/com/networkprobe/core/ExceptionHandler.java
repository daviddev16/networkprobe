package com.networkprobe.core;

import org.slf4j.Logger;

public final class ExceptionHandler {

    /**
     * O método unexpected deve ser usado quando uma exceção é lançada indevidamente e por isso,
     * deve-se feito o encerramento da aplicação para análise.
     *
     * @param logger O objeto de log da classe que está lançando a exceção.
     * @param exception A exceção que não deveria ser lançada.
     * @param exitCode O código de encerramento da aplicação.
     * */
    public static void unexpected(Logger logger, Exception exception, int exitCode) {
        logger.error("Uma exceção não esperada foi lançanda, encerrando.");
        logger.error(exception.getClass().getSimpleName() + " { " + exception.getMessage() + " }");
        Runtime.getRuntime().exit(exitCode);
    }

}
