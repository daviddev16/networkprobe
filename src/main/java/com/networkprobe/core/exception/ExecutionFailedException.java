package com.networkprobe.core.exception;

public class ExecutionFailedException extends RuntimeException {

    public ExecutionFailedException() {}

    public ExecutionFailedException(String message) {
        super(message);
    }

    public ExecutionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionFailedException(Throwable cause) {
        super(cause);
    }
}
