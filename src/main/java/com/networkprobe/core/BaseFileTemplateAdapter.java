package com.networkprobe.core;

import com.networkprobe.core.annotation.reflections.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.networkprobe.core.SingletonDirectory.getSingleOf;
import static com.networkprobe.core.util.Validator.checkIsReadable;
import static com.networkprobe.core.util.Validator.nonNull;

@Singleton(creationType = SingletonType.DYNAMIC, order = 250)
public class BaseFileTemplateAdapter
        extends BaseConfigurableTemplate implements TemplateFileAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(BaseFileTemplateAdapter.class);

    private File templateFile;
    private ResponseEntityFactory responseEntityFactory;

    @Override
    public void load(File templateFile, ResponseEntityFactory responseEntityFactory) {

        try {

            this.templateFile = checkIsReadable(templateFile, "templateFile");
            this.responseEntityFactory = nonNull(responseEntityFactory, "responseEntityFactory");

            /* Localiza o carregador adequado para o tipo de arquivo */
            FileTemplateLoader templateLoader = getSingleOf(FileTemplateLoaderFactory.class)
                    .chooseLoaderFrom(templateFile);

            /* verifica se é nulo e carrega as informações do arquivo no template */
            nonNull(templateLoader, "templateLoader")
                    .load(templateFile, this, responseEntityFactory);

            LOG.info("O arquivo \"{}\" foi carregado com sucesso pelo adaptador " +
                    "\"{}\".", templateFile.getName(), templateLoader.getAdapterName());

            templateLoader.onSuccessfully();
        }
        catch (Exception exception) {
            ExceptionHandler.handleUnexpected(LOG, exception, Reason.NPS_FILE_LOADER_EXCEPTION);
        }

    }

    @Override
    public void reload() {
        load(templateFile, responseEntityFactory);
    }

}
