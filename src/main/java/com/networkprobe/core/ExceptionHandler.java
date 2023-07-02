package com.networkprobe.core;

import org.slf4j.Logger;

public final class ExceptionHandler {

    public static void unexpected(Logger logger, Exception exception, int exitCode) {
        logger.error("Uma exceção não esperada foi lançanda, encerrando.", exception);
        Runtime.getRuntime().exit(exitCode);
    }

}
