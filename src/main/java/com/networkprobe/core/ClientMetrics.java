package com.networkprobe.core;

import com.networkprobe.core.api.Template;
import com.networkprobe.core.util.Validator;

public class ClientMetrics {

    private volatile int udpReceivedCount = 0;
    private volatile int tcpConnectionCount = 0;

    public ClientMetrics() {}

    public void updateClientMetric(ClientMetricType clientMetricType) {
        Validator.checkIsNotNull(clientMetricType, "clientMetricType");
        switch (clientMetricType) {
            case UDP_RECEIVED:
                udpReceivedCount += 1;
                break;
            case TCP_CONNECTION:
                tcpConnectionCount += 1;
                break;
        }
    }

    public int getTcpConnectionCount() {
        return tcpConnectionCount;
    }

    public int getUdpReceivedCount() {
        return udpReceivedCount;
    }
}
