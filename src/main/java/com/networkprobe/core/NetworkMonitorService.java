package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton(creationType = SingletonType.DYNAMIC, order = -500)
public final class NetworkMonitorService extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkMonitorService.class);
    private final Map<Integer, ClientMetrics> metrics = Collections.synchronizedMap(new HashMap<>());
    private static NetworkMonitorService monitorServiceInstance;
    private static final long METRICS_TIMEOUT = 1;

    public NetworkMonitorService()
    {
        super("network-monitor-worker", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    protected void onUpdate() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(METRICS_TIMEOUT));
            //metrics.clear();
        } catch (InterruptedException e) {
            ExceptionHandler.unexpected(LOG, e, 202);
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

    public static Map<Integer, ClientMetrics> getAllMetrics() {
        return getMonitor().getMetrics();
    }

    public Map<Integer, ClientMetrics> getMetrics() {
        return metrics;
    }

    public static NetworkMonitorService getMonitor() {
        return (monitorServiceInstance != null) ? monitorServiceInstance :
                (monitorServiceInstance = SingletonDirectory.getSingleOf(NetworkMonitorService.class));
    }
}