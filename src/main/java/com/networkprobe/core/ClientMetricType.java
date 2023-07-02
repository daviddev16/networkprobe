package com.networkprobe.core;

public enum ClientMetricType {

    UDP_RECEIVED   ( 48),
    TCP_CONNECTION (100);

    private final int limit;

    private ClientMetricType(int limit)
    {
        this.limit = (limit < 1) ? 1024 : limit;
    }

    public int getLimit() {
        return limit;
    }
}