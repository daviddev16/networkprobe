package com.networkprobe.core.entity;

/** Classe de instância padrão para ResponseEntity */
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
