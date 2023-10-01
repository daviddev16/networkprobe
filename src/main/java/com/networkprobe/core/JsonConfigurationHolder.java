package com.networkprobe.core;

import org.json.JSONObject;

public class JsonConfigurationHolder extends ConfigurationHolder {

    public JsonConfigurationHolder(JSONObject jsonObject, BaseConfigurableTemplate configurableTemplate) {
        super(jsonObject, configurableTemplate);
    }

    public JSONObject getJSONObject() {
        //if (!(configurationPlaceholder instanceof JSONObject))
        //    inconsistentConfigurationTypeException(JSONObject.class);
        return (JSONObject) configurationPlaceholder;
    }

}
