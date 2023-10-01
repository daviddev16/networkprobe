package com.networkprobe.core.statistics;

public enum Metric {

    UDP_RECEIVED_COUNT("udp_received_count"),
    TCP_ACCEPTED_COUNT("tcp_accepted_count"),
    UDP_BLOCKED_COUNT("udp_blocked_count"),
    TCP_BLOCKED_COUNT("tcp_blocked_count"),
    LAST_TIME_CONNECTED("tcp_last_time_connected");

    private final String codeName;

    Metric(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

}