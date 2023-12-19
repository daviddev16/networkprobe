package com.networkprobe.core.exception;

import com.networkprobe.core.annotation.miscs.Documented;
import org.json.JSONObject;

@Documented(done = false)
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

    public static String wrapToJson(Exception exception) {
        return new JSONObject()
                .put("message", exception.getMessage())
                .toString();
    }

    public String toJSONMessage() {
        return wrapToJson(this);
    }
}
