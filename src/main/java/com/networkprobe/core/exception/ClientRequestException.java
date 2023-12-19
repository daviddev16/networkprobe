package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public class ClientRequestException extends JsonRuntimeException {

    public ClientRequestException(String message) {
        super(message);
    }

}
