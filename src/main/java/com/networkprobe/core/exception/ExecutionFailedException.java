package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
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
