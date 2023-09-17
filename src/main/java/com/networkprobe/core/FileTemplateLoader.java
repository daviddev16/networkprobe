package com.networkprobe.core;

import java.io.File;
import java.io.IOException;

public interface FileTemplateLoader extends BaseTemplateLoader {

    ConfigurationHolder getConfigurator(File file,
                                        BaseConfigurableTemplate configurableTemplate)  throws IOException;

}
