package com.networkprobe.core.factory;

import com.networkprobe.core.command.caching.ResponseEntity;

public interface ResponseEntityFactory {

    ResponseEntity<?> responseEntityOf(String rawContent, boolean cachedOnce);

}
