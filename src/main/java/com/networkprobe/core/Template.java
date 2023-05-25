package com.networkprobe.core;

import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.config.model.Command;
import org.jetbrains.annotations.Nullable;

import static com.networkprobe.core.util.Validator.checkIsNotNull;

public final class Template {

    private final TemplateLoader templateLoader;

    public Template(TemplateLoader templateLoader) {
        checkIsNotNull(templateLoader, "templateLoader");
        checkIsNotNull(templateLoader.getCommands(), "templateLoader->getCommands");
        checkIsNotNull(templateLoader.getRoutes(), "templateLoader->getRoutes");
        checkIsNotNull(templateLoader.getNetworking(), "templateLoader->getNetworking");
        this.templateLoader = templateLoader;
    }

    @Nullable
    public Command getCommand(String name) {
        return getTemplateLoader().getCommands().getOrDefault(name, null);
    }

    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }


}
