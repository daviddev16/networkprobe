package com.networkprobe.core.exception;

public final class DependencyException extends RuntimeException {

    public DependencyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DependencyException(String message) {
        super(message);
    }

}