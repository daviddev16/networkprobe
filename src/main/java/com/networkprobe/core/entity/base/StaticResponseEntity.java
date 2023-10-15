package com.networkprobe.core.entity.base;

import com.networkprobe.core.entity.base.DefaultResponseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * O valor de um StaticResponseEntity não muda durante o tempo de
 * execução e sempre retornará um objeto do tipo String.
 *  */
public class StaticResponseEntity extends DefaultResponseEntity<String> {

    public StaticResponseEntity(String rawContent) {
        super(rawContent);
    }

    @Override
    public @NotNull String getContent(List<String> arguments) {
        return getRawContent() != null ? getRawContent() : "";
    }

    @Override
    public boolean isCachedOnce() {
        return true;
    }

}
