package com.networkprobe.core;

import com.networkprobe.core.exception.InvalidPropertyException;
import org.json.JSONObject;

import java.util.Map;

import static com.networkprobe.core.util.Validator.nonNull;

public class ConfigurationHolder {

    private final BaseConfigurableTemplate configurableTemplate;
    public final Object configurationPlaceholder;

    public ConfigurationHolder(Object configurationPlaceholder, BaseConfigurableTemplate configurableTemplate) {
        this.configurationPlaceholder = nonNull(configurationPlaceholder, "configurationPlaceholder");
        this.configurableTemplate = nonNull(configurableTemplate, "configurationPlaceholder");
    }

    protected void inconsistentConfigurationTypeException(Class<?> expected) {
        throw new InvalidPropertyException("Não foi possível configurar um template por arquivo. " +
                "O tipo do configurador não condiz com \"" + expected.getName() + "\", que é o esperado.");
    }

    public BaseConfigurableTemplate getConfigurableTemplate() {
        return configurableTemplate;
    }

    public static ConfigurationHolder nullable() {
        return new ConfigurationHolder(null, null);
    }
}
