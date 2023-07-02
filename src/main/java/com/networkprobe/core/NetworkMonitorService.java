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

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMonitorService.class);
    private final Map<String, ClientMetrics> metrics = Collections.synchronizedMap(new HashMap<>());

    /* 30 minutos de monitoramento para a limpeza de métricas */
    private static final long METRICS_TIMEOUT = 60 * 30;

    public NetworkMonitorService()
    {
        super("network-monitor-worker", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    protected void onUpdate() {
        try {
            metrics.clear();
            Thread.sleep(TimeUnit.SECONDS.toMillis(METRICS_TIMEOUT));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static NetworkMonitorService getMonitor() {
        return SingletonDirectory.getSingleOf(NetworkMonitorService.class);
    }
}