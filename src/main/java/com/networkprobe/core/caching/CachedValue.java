package com.networkprobe.core.caching;

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

    public static CachedValue createInstant(String value) {
        final long timestamp = System.currentTimeMillis();
        return new CachedValue(value, timestamp);
    }

}
