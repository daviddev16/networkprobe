package com.networkprobe.core.caching;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Proxy de caching de processamento do ResponseEntity baseado em tempo,
 * não necessita de complexidade extra para caching de dados que não vão
 * mudar com frequência, como por exemplo um IPv4 endereçado por um servidor
 * DHCP ou nome do banco de dados que não irá mudar.
 * */
public abstract class CachedResponseEntity extends DefaultResponseEntity<String> {

    private final AtomicReference<CachedValue> cachedValue = new AtomicReference<>();
    public static final long CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(2);
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
