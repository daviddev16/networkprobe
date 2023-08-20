package com.networkprobe.core.entity;

import org.jetbrains.annotations.NotNull;

/**
 * O valor de um StaticResponseEntity não muda durante o tempo de
 * execução e sempre retornará um objeto do tipo String.
 *  */
public class StaticResponseEntity extends DefaultResponseEntity<String> {

    public StaticResponseEntity(String rawContent) {
        super(rawContent);
    }

    @Override
    public @NotNull String getContent() {
        return getRawContent() != null ? getRawContent() : "";
    }

    @Override
    public boolean isCachedOnce() {
        return true;
    }

}
