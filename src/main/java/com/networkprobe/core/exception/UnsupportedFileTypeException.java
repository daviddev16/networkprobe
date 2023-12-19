package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException(String message) {
        super(message);
    }

}
