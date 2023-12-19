package com.networkprobe.core.statistics;

import com.networkprobe.core.annotation.miscs.Documented;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Documented(done = false)
public class ClientMetrics {

    private final Map<Metric, Object> metrics = new HashMap<>();

    public ClientMetrics() {
        setupInitialMetricsValues();
    }

    private void setupInitialMetricsValues() {
        metrics.put(Metric.UDP_RECEIVED_COUNT, new SpecialIntegerValue());
        metrics.put(Metric.TCP_ACCEPTED_COUNT, new SpecialIntegerValue());
        metrics.put(Metric.UDP_BLOCKED_COUNT, new SpecialIntegerValue());
        metrics.put(Metric.TCP_BLOCKED_COUNT, new SpecialIntegerValue());
        metrics.put(Metric.COMMAND_HISTORY, new ArrayDeque<String>());
    }

    public void pushCommandToClientCommandHistory(String command) {
        ArrayDeque<String> commandHistory = (ArrayDeque<String>) metrics.get(Metric.COMMAND_HISTORY);
        if (commandHistory.size() >= 3) {
            commandHistory.removeLast();
        }
        commandHistory.push(command);
    }

    public void upgradeMetricValue(Metric metric) {
        Object value = metrics.get(metric);
        if (value instanceof SpecialIntegerValue)
            ((SpecialIntegerValue) value).increment();
    }

    public int getIntegerValueOf(Metric metric) {
        Object value = metrics.get(metric);
        if (value instanceof SpecialIntegerValue) {
            return ((SpecialIntegerValue) value).incrementAndGet();
        }
        return -1;
    }

    public boolean checkThresholdAndUpdate(Metric metric, int threshold) {
        if (getIntegerValueOf(metric) <= threshold) {
            upgradeMetricValue(metric);
            return true;
        }
        return false;
    }

    public void setAddressIfAbsent(String hostAddress) {
        metrics.putIfAbsent(Metric.INET4_ADDRESS, hostAddress);
    }

    public void storeLastTimeConnected() {
        metrics.put(Metric.LAST_TIME_CONNECTED, LocalDateTime.now());
    }

    public void cleanUpMetrics() {
        metrics.clear();
        setupInitialMetricsValues();
    }

    public void forEachMetric(BiConsumer<Metric, Object> biConsumer) {
        Objects.requireNonNull(biConsumer);
        metrics.forEach(biConsumer);
    }

    public void update(Metric metric, Object value) {
        metrics.put(metric, value);
    }

}
