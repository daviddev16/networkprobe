package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public class InvalidPropertyException extends RuntimeException {

    public InvalidPropertyException(String message) {
        super(message);
    }

}
