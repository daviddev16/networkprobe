package com.networkprobe.core;

import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.threading.ExecutionWorker;

public abstract class NetworkService extends ExecutionWorker {

    private final TemplateLoader templateLoader;

    public NetworkService(String name, boolean updatable, boolean daemon, TemplateLoader templateLoader) {
        super(name, updatable, daemon);
        this.templateLoader = templateLoader;
    }

    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }
}
