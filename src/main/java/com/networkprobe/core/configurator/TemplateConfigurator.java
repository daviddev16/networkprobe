package com.networkprobe.core.configurator;

import com.networkprobe.core.*;
import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.configurator.Configurable;


/**
 * TemplateConfigurator faz o carregamento do arquivo de template para o template base
 * da aplicação. Esse objeto somente é instânciado após todo o carregamento do sistema.
 **/
@Documented
@Singleton(creationType = SingletonType.DYNAMIC, order = 2000)
public class TemplateConfigurator implements Configurable {

    public TemplateConfigurator() {
        SingletonDirectory.denyInstantiation(this);
    }

    /**
     * Carrega o template do arquivo passado na linha de comando, utilizando o {@link CommandResponseFactory}
     * para determinar qual será o carregador do arquivo, verificando de acordo com a extensão do arquivo.
     **/
    @Override
    public void configure() {
        BaseFileTemplateAdapter templateAdapter = SingletonDirectory.getSingleOf(BaseFileTemplateAdapter.class);
        templateAdapter.load(NetworkProbeOptions.getTemplateFile(), CommandResponseFactory.getInstance());
    }

}
