package com.networkprobe.core.exception;

import org.json.JSONObject;

public class JsonRuntimeException extends RuntimeException {

    public JsonRuntimeException() {}

    public JsonRuntimeException(String message) {
        super(message);
    }

    public JsonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonRuntimeException(Throwable cause) {
        super(cause);
    }

    public JsonRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static String wrap(Exception exception, String origin) {
        return String.format("{@}:%s:%s", origin,
                new JSONObject()
                        .put("message", exception.getMessage())
                .toString());
    }

    public String toJSONMessage(String origin) {
        return wrap(this, origin);
    }
}
