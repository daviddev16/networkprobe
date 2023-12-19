package com.networkprobe.core.entity.caching;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.entity.base.DefaultResponseEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Proxy de caching de processamento do ResponseEntity baseado em tempo,
 * não necessita de complexidade extra para caching de dados que não vão
 * mudar com frequência, como por exemplo um IPv4 endereçado por um servidor
 * DHCP ou nome do banco de dados que não irá mudar.
 * */
@Documented(done = false)
public abstract class CachedResponseEntity extends DefaultResponseEntity<String> {

    private final AtomicReference<CachedValue> cachedValue = new AtomicReference<>();
    public static final long CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(2);
    private final boolean cachedOnce;

    public CachedResponseEntity(String rawContent, boolean cachedOnce) {
        super(rawContent);
        cachedValue.set(new CachedValue("", 0));
        this.cachedOnce = cachedOnce;
    }

    public abstract void cache();

    @Override
    public String getContent(List<String> arguments) {

       if (!isCachedOnce() &&  getCachedValue().getElapsedTime()>= CACHE_TIMEOUT)
           cache();

        return getCachedValue().getValue();
    }

    public void setCachedValue(CachedValue cachedValue) {
        this.cachedValue.set(cachedValue);
    }

    public CachedValue getCachedValue() {
        return cachedValue.get();
    }


    @Override
    public boolean isCachedOnce() {
        return cachedOnce;
    }
}
