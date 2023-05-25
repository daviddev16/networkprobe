package com.networkprobe.core.api;

public interface ResponseEntity<T> {

    String getRawContent();

    T getContent();

    boolean isCachedOnce();

}
