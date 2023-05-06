package com.networkprobe.core.exception;

import org.slf4j.Logger;

import java.io.File;

public class Exceptions {

    public static final boolean SHOW_STACKTRACE = false;

    public static InvalidPropertyException incoherentPropertyTypeException(String message) {
        return new InvalidPropertyException( message );
    }

    public static void fileVerificationMessage(Logger logger, File file, Exception exception) {
        logger.error("Dentro do arquivo \"{}\", verifique a seguinte informação: \n\n{}\n\n", file.getName(),
                exception.getMessage());
        if (SHOW_STACKTRACE)
            logger.error("Stacktrace:", exception);
    }
}
