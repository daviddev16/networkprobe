package com.networkprobe.core.networking;

import com.networkprobe.core.DiscoveryServer;
import com.networkprobe.core.ExchangeServer;
import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;

public class NetworkServicesFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServicesFacade.class);

    private static NetworkServicesFacade instance;

    private ExchangeServer exchangeServer;
    private DiscoveryServer discoveryServer;

    private NetworkServicesFacade() { instance = this; }

    public void launchAllServices(TemplateLoader templateLoader) {

        if (templateLoader.getNetworking().isDiscoveryEnabled())
            createAndStartBroadcastServer(templateLoader);
        else
            LOGGER.info("O serviço de descoberta de rede foi desativado nas configurações.");

        createAndStartExchangeServer(templateLoader);
    }

    private void createAndStartExchangeServer(TemplateLoader templateLoader) {
        if (exchangeServer == null) {
            exchangeServer = new ExchangeServer(templateLoader);
            exchangeServer.start();
        }
    }

    private void createAndStartBroadcastServer(TemplateLoader templateLoader) {
        if (discoveryServer == null) {
            discoveryServer = new DiscoveryServer(templateLoader);
            discoveryServer.start();
        }
    }

    public static NetworkServicesFacade getInstance() {
        return instance;
    }

    public static void initialize() throws InstanceAlreadyExistsException {
        if (instance != null)
            throw ExceptionHandler.instanceAlreadyExists(NetworkServicesFacade.class);
        new NetworkServicesFacade();
    }
}
