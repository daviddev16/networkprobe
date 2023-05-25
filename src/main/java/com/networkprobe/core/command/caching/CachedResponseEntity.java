package com.networkprobe.core.command.caching;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* time based cache system */
public abstract class CachedResponseEntity extends DefaultResponseEntity<String> {

    public static final long CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(2);

    private final AtomicReference<CachedValue> cachedValue = new AtomicReference<>();
    private final boolean cachedOnce;

    public CachedResponseEntity(String rawContent, boolean cachedOnce) {
        super(rawContent);
        this.cachedOnce = cachedOnce;
    }

    abstract void cache();

    @Override
    public String getContent() {

        if (!isCachedOnce() && getElapsedTime()>= CACHE_TIMEOUT)
            cache();

        return getCachedValue().getValue();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - getCachedValue().getTimestamp();
    }

    public void setCachedValue(CachedValue cachedValue) {
        this.cachedValue.lazySet(cachedValue);
    }

    public CachedValue getCachedValue() {
        return cachedValue.get();
    }

    @Override
    public boolean isCachedOnce() {
        return cachedOnce;
    }
}
