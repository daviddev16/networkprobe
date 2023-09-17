package com.networkprobe.core;

import com.networkprobe.core.exception.InvalidPropertyException;
import org.json.JSONObject;

import java.util.Map;

import static com.networkprobe.core.util.Validator.nonNull;

public final class ConfigurationHolder {

    private final BaseConfigurableTemplate configurableTemplate;
    public final Object configurationPlaceholder;

    private ConfigurationHolder(Object configurationPlaceholder, BaseConfigurableTemplate configurableTemplate) {
        this.configurationPlaceholder = nonNull(configurationPlaceholder, "configurationPlaceholder");
        this.configurableTemplate = nonNull(configurableTemplate, "configurationPlaceholder");
    }

    public JSONObject getJSONObject() {

        if (!(configurationPlaceholder instanceof JSONObject))
            inconsistentConfigurationTypeException(JSONObject.class);

        return (JSONObject) configurationPlaceholder;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getYAMLMap() {

        if (!(configurationPlaceholder instanceof Map))
            inconsistentConfigurationTypeException(Map.class);

        return (Map<String, Object>) configurationPlaceholder;
    }

    public BaseConfigurableTemplate getConfigurableTemplate() {
        return configurableTemplate;
    }

    private void inconsistentConfigurationTypeException(Class<?> expected) {
        throw new InvalidPropertyException("Não foi possível configurar um template por arquivo. " +
                "O tipo do configurador não condiz com \"" + expected.getName() + "\", que é o esperado.");
    }

    public static ConfigurationHolder of(Object configurationPlaceholder,
                                         BaseConfigurableTemplate configurableTemplate)
    {
        return new ConfigurationHolder(configurationPlaceholder, configurableTemplate);
    }

    public static ConfigurationHolder dummy() {
        return new ConfigurationHolder(new Object(), null);
    }

}
