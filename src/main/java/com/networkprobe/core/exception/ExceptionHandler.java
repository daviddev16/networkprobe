package com.networkprobe.core.exception;

import org.slf4j.Logger;

import javax.management.InstanceAlreadyExistsException;
import java.util.Objects;

import static java.lang.String.*;

public final class ExceptionHandler {

    public static void unexpected(Logger logger, Exception exception, int exitCode) {
        Objects.requireNonNull(exception, "Objeto da Exception não pode ser nulo.");
        logger.error("Uma exceção não esperada foi lançanda, encerrando.", exception);
        Runtime.getRuntime().exit(exitCode);
    }

    public static final InstanceAlreadyExistsException instanceAlreadyExists(Class<?> clazz) {
        return new InstanceAlreadyExistsException(format("Uma instância de \"%s\" já existe.", clazz
                .getName()));
    }

}
