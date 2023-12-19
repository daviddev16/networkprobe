package com.networkprobe.core.statistics;

import com.networkprobe.core.annotation.miscs.Documented;

@Documented(done = false)
public enum Metric {

    INET4_ADDRESS("inet4_address"),
    UDP_RECEIVED_COUNT("udp_received_count"),
    TCP_ACCEPTED_COUNT("tcp_accepted_count"),
    UDP_BLOCKED_COUNT("udp_blocked_count"),
    TCP_BLOCKED_COUNT("tcp_blocked_count"),
    LAST_TIME_CONNECTED("tcp_last_time_connected"),
    COMMAND_HISTORY("command_history");

    private final String codeName;

    Metric(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

}