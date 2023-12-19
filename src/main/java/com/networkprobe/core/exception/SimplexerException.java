package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public class SimplexerException extends RuntimeException {

    public SimplexerException() {
        super();
    }

    public SimplexerException(String message) {
        super(message);
    }

}
