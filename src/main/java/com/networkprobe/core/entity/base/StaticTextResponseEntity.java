package com.networkprobe.core.entity.base;

import com.networkprobe.core.annotation.miscs.Documented;

import java.util.List;

/**
 * O valor de um StaticTextResponseEntity não muda durante o tempo de
 * execução e sempre retornará um objeto do tipo String.
 *  */
@Documented(done = false)
public class StaticTextResponseEntity extends DefaultResponseEntity<String> {

    public StaticTextResponseEntity(String rawContent) {
        super(rawContent);
    }

    @Override
    public String getContent(List<String> arguments) {
        return getRawContent() != null ? getRawContent() : "";
    }

    @Override
    public boolean isCachedOnce() {
        return true;
    }

}
