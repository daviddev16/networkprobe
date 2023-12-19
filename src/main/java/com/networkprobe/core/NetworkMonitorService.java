package com.networkprobe.core;

import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.statistics.Metric;
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
    private static final long METRICS_TIMEOUT = 30;

    public NetworkMonitorService() {
        super("network-monitor-worker", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    protected void onUpdate() {
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(METRICS_TIMEOUT));
            getMapMetrics().clear();
        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.NPS_NETWORK_MONITOR_UPDATE);
        }
    }

    public ClientMetrics getInetMetrics(int simplifiedAddress) {
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
        ClientMetrics clientMetrics = getInetMetrics(address);
        clientMetrics.setAddressIfAbsent(inetAddress.getHostAddress());
        return clientMetrics;
    }

    public Map<Integer, ClientMetrics> getMapMetrics() {
        return metrics;
    }

    public static NetworkMonitorService getMonitorService() {
        return (networkMonitorServiceInstance != null) ? networkMonitorServiceInstance :
                (networkMonitorServiceInstance = SingletonDirectory.getSingleOf(NetworkMonitorService.class));
    }
}