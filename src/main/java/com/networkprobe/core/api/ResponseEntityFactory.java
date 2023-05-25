package com.networkprobe.core.api;

public interface ResponseEntityFactory {

    ResponseEntity<?> responseEntityOf(String rawContent, boolean cachedOnce);

}
