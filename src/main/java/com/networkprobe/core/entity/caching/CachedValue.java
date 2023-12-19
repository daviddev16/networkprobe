package com.networkprobe.core.entity.caching;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public class CachedValue {

    private final String value;
    private final long timestamp;

    public CachedValue(String value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - getTimestamp();
    }

    public static CachedValue createInstant(String value) {
        return new CachedValue(value, System.currentTimeMillis());
    }

}
