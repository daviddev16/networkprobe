package com.networkprobe.core;

import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  NetworkServicesFacade faz a inicialização do serviço de descoberta e o serviço de troca
 **/
@Singleton(creationType = SingletonType.DYNAMIC, order = -501)
public class NetworkServicesFacade {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkServicesFacade.class);
    private static NetworkServicesFacade networkServicesFacadeInstance;

    @ManagedDependency
    private Template template;

    public NetworkServicesFacade()
    {
        SingletonDirectory.denyInstantiation(this);
    }

    public void launchAllServices() {

        if (NetworkProbeOptions.isDebugSocketEnabled())
            LOG.warn("O modo DEBUG_SOCKET pode causar queda na perfomance da aplicação, utilize somente se for necessário.");

        NetworkMonitorService monitorService = NetworkMonitorService.getMonitorService();
        monitorService.start();

        NetworkExchangeService exchangeService = NetworkExchangeService.getExchangeService();
        exchangeService.start();

        if (!template.getNetworking().isDiscoveryEnabled())
            LOG.warn("O serviço de descoberta de rede foi desativado nas configurações.");

        else {
            NetworkDiscoveryService discoveryService = NetworkDiscoveryService.getDiscoveryService();
            discoveryService.start();
        }
    }

    public static NetworkServicesFacade getNetworkServices() {
        return SingletonDirectory.getSingleOf(NetworkServicesFacade.class);
    }

}
