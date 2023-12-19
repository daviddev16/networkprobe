package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public final class SingletonException extends RuntimeException {

    public SingletonException(Class<?> objectClass, String reason) {
        super(String.format("Violação na regra de instância da classe \"%s\". { ESPEC: %s }",
                objectClass.getSimpleName(), reason));
    }

    public SingletonException(String message, Exception exception) {
        super(message);
        addSuppressed(exception);
    }
}