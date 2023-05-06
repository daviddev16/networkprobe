package com.networkprobe.core.command.caching;

public interface ResponseEntity<T> {

    String getRawContent();

    T getContent();

    boolean isCachedOnce();

}
