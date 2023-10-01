package com.networkprobe.core;

import com.networkprobe.core.adapter.FileTemplateAdapter;
import com.networkprobe.core.annotation.Singleton;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.networkprobe.core.util.Validator.checkIsReadable;
import static com.networkprobe.core.util.Validator.nonNull;

@Singleton(creationType = SingletonType.DYNAMIC, order = 250)
public class BaseFileTemplateAdapter
        extends BaseConfigurableTemplate implements FileTemplateAdapter {

    private FileTemplateLoader templateLoader;

    @Override
    public void load(File file, ResponseEntityFactory responseEntityFactory) {
        try {

            checkIsReadable(file, "file");
            nonNull(templateLoader, "templateLoader");
            nonNull(responseEntityFactory, "responseEntityFactory");

            ConfigurationHolder configurationHolder = templateLoader.getConfigurator(file, this);
            configurationHolder = (configurationHolder != null) ? configurationHolder :
                    ConfigurationHolder.nullable();

            templateLoader.loadNetworkingSettings(configurationHolder);
            templateLoader.loadRouteSettings(configurationHolder);
            templateLoader.loadCommandSettings(configurationHolder, responseEntityFactory);

            LoggerFactory.getLogger(getClass()).info("O arquivo \"{}\" foi carregado com " +
                    "sucesso pelo adaptador \"{}\".", file.getName(), templateLoader.getAdapterName());

            getTemplateLoader().onSuccessfully();

        } catch (Exception exception) {
            if (getTemplateLoader().exceptionHandler(exception))
                Runtime.getRuntime().exit(155);
        }
    }

    public void setFileTemplateLoader(FileTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public BaseTemplateLoader getTemplateLoader() {
        return templateLoader;
    }

}
