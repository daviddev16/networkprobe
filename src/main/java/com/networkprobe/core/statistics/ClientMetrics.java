package com.networkprobe.core.statistics;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientMetrics {

    private final Map<Metric, Object> metrics = new HashMap<>();

    public ClientMetrics() {
        clear(); /* init values */
    }

    public void clear() {
        metrics.put(Metric.UDP_RECEIVED_COUNT, new AtomicInteger(0));
        metrics.put(Metric.TCP_ACCEPTED_COUNT, new AtomicInteger(0));
        metrics.put(Metric.UDP_BLOCKED_COUNT, new AtomicInteger(0));
        metrics.put(Metric.TCP_BLOCKED_COUNT, new AtomicInteger(0));
        metrics.put(Metric.LAST_TIME_CONNECTED, LocalDateTime.now());
    }

    public void upgrade(Metric metric) {
        Object value = metrics.get(metric);
        if (value instanceof AtomicInteger) {
            ((AtomicInteger) value).incrementAndGet();
        }
    }

    public int get(Metric metric) {
        Object value = metrics.get(metric);
        if (value instanceof AtomicInteger) {
            return ((AtomicInteger) value).get();
        }
        return -1;
    }

    public boolean checkAndUpdate(Metric metric, int threshold) {
        int currentValue = get(metric);
        if (currentValue <= threshold) {
            upgrade(metric);
            return true;
        }
        return false;
    }

    public void update(Metric metric, Object value) {
        metrics.put(metric, value);
    }

}
