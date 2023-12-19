package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  NetworkServicesFacade faz a inicialização do serviço de descoberta e o serviço de troca
 **/
@Singleton(creationType = SingletonType.DYNAMIC, order = -501)
@Documented(done = false)
public class NetworkServicesFacade {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkServicesFacade.class);
    private static NetworkServicesFacade networkServicesFacadeInstance;

    @Handled
    private Template template;

    public NetworkServicesFacade() {
        SingletonDirectory.denyInstantiation(this);
    }

    public void launchAllServices() {

        if (NetworkProbeOptions.isDebugSocketEnabled())
            LOG.warn("O modo DEBUG_SOCKET pode causar queda na perfomance da aplicação, utilize somente se for necessário.");

        NetworkMonitorService monitorService = NetworkMonitorService.getMonitorService();
        monitorService.start();

        NetworkExchangeService exchangeService = NetworkExchangeService.getExchangeService();
        exchangeService.start();

        if (template.getNetworking().isDiscoveryEnabled()){
            NetworkDiscoveryService discoveryService = NetworkDiscoveryService.getDiscoveryService();
            discoveryService.start();
        }
        else {
            LOG.warn("O serviço de descoberta de rede foi desativado nas configurações.");
        }
    }

    public static NetworkServicesFacade getNetworkServices() {
        return SingletonDirectory.getSingleOf(NetworkServicesFacade.class);
    }

}
