package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton(creationType = SingletonType.DYNAMIC, order = -500)
public final class NetworkMonitorService extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkMonitorService.class);
    private static NetworkMonitorService networkMonitorServiceInstance;
    private final Map<Integer, ClientMetrics> metrics = new HashMap<>();
    private static final long METRICS_TIMEOUT = 5;

    public NetworkMonitorService()
    {
        super("network-monitor-worker", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    protected void onUpdate() {
        try {
            Thread.sleep(TimeUnit.HOURS.toMillis(METRICS_TIMEOUT));
            getMetrics().clear();
        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.NPS_NETWORK_MONITOR_UPDATE);
        }
    }

    public ClientMetrics getMetrics(int simplifiedAddress) {
        ClientMetrics clientMetrics = metrics.get(simplifiedAddress);
        if (clientMetrics == null) {
            clientMetrics = new ClientMetrics();
            metrics.put(simplifiedAddress, clientMetrics);
        }
        return clientMetrics;
    }

    /* converte InetAddress em um Integer de 32 bits para ser usado no Map de métricas */
    public ClientMetrics getMetrics(InetAddress inetAddress) {
        int address = Utility.convertInetToInterger(inetAddress);
        return getMetrics(address);
    }

    public Map<Integer, ClientMetrics> getMetrics() {
        return metrics;
    }

    public static NetworkMonitorService getMonitorService() {
        return (networkMonitorServiceInstance != null) ? networkMonitorServiceInstance :
                (networkMonitorServiceInstance = SingletonDirectory.getSingleOf(NetworkMonitorService.class));
    }
}