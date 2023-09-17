package com.networkprobe.core.configurator;

import com.networkprobe.core.*;
import com.networkprobe.core.annotation.Singleton;

import java.io.File;

/*
* Configurator's
* Configuradores serão usados para configurar trechos de códigos dinâmicamente, sem poluir o Launcher
* */
@Singleton(creationType = SingletonType.DYNAMIC, order = 2000)
public class TemplateConfigurator {

    public TemplateConfigurator() { configure(); }

    private void configure()
    {
        File templateFile = NetworkProbeOptions.getTemplateFile();

        BaseFileTemplateAdapter templateAdapter = SingletonDirectory
                .getSingleOf(BaseFileTemplateAdapter.class);

        templateAdapter.setFileTemplateLoader(FileTemplateLoaderFactory.getFactory()
                .chooseLoaderFrom(templateFile));

        templateAdapter.load(templateFile, CommandResponseFactory.getFactory());
        Defaults.createDefaultApplicationCmds(templateAdapter);
    }

}
