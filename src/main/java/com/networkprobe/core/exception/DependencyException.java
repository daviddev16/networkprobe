package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public final class DependencyException extends RuntimeException {

    public DependencyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DependencyException(String message) {
        super(message);
    }

}