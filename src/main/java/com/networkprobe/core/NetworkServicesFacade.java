package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  NetworkServicesFacade faz a inicialização do serviço de descoberta e o serviço de troca
 **/
@Singleton(creationType = SingletonType.DYNAMIC, order = -501)
public class NetworkServicesFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServicesFacade.class);

    public NetworkServicesFacade()
    {
        SingletonDirectory.denyInstantiation(this);
    }

    public void launchAllServices() throws Exception {

        NetworkMonitorService monitorService = NetworkMonitorService.getMonitor();
        monitorService.start();

        NetworkExchangeService exchangeService = NetworkExchangeService.getExchangeService();
        exchangeService.start();

        if (!JsonTemplateAdapter.getTemplateInstance().getNetworking().isDiscoveryEnabled())
            LOGGER.info("O serviço de descoberta de rede foi desativado nas configurações.");

        else {
            NetworkDiscoveryService discoveryService = NetworkDiscoveryService.getDiscoveryService();
            discoveryService.start();
        }
    }

    public static NetworkServicesFacade getNetworkServices() {
        return SingletonDirectory.getSingleOf(NetworkServicesFacade.class);
    }

}
