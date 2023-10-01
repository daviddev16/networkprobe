package com.networkprobe.core;

public interface BaseTemplateLoader {

    void onSuccessfully();

    boolean exceptionHandler(Exception exception);

    void loadRouteSettings(ConfigurationHolder configurationHolder);

    void loadNetworkingSettings(ConfigurationHolder configurationHolder);

    void loadCommandSettings(ConfigurationHolder configurationHolder,
                             ResponseEntityFactory responseEntityFactory);

    default String getAdapterName() {
        return getClass().getSimpleName();
    }

}
