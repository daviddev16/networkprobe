package com.networkprobe.core.entity;

import com.networkprobe.core.SingletonType;
import com.networkprobe.core.Template;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.ClientRequestException;

import java.util.List;

@Singleton(creationType = SingletonType.DYNAMIC, order = 201)
public class QueryTagResponseEntity implements ResponseEntity<String> {

    @ManagedDependency
    private Template template;

    @Override
    public String getRawContent() {
        return null;
    }

    @Override
    public String getContent(List<String> arguments) {

        if (arguments.isEmpty())
            throw new ClientRequestException("Não foi informado tags para pesquisa.");

        return "";
    }

    @Override
    public boolean isCachedOnce() {
        return true;
    }
}
