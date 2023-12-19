package com.networkprobe.core.entity.base;

import com.networkprobe.core.annotation.miscs.Documented;

/** Classe de instância padrão para ResponseEntity */
@Documented(done = false)
public abstract class DefaultResponseEntity<T> implements ResponseEntity<T> {

    private final String rawContent;

    public DefaultResponseEntity(String rawContent) {
        this.rawContent = rawContent;
    }

    @Override
    public String getRawContent() {
        return rawContent;
    }

}
