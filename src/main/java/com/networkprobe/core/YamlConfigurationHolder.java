package com.networkprobe.core;

import com.amihaiemil.eoyaml.YamlMapping;

public class YamlConfigurationHolder extends ConfigurationHolder {

    public YamlConfigurationHolder(YamlMapping configurationPlaceholder,
                                   BaseConfigurableTemplate configurableTemplate) {
        super(configurationPlaceholder, configurableTemplate);
    }

    @SuppressWarnings("unchecked")
    public YamlMapping getYAMLMapping() {
        return (YamlMapping) configurationPlaceholder;
    }

}
