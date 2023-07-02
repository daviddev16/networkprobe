package com.networkprobe.core;

import com.networkprobe.core.util.Validator;

public class ClientMetrics {

    private volatile int udpReceivedCount = 0;
    private volatile int tcpConnectionCount = 0;

    public ClientMetrics() {}

    /* TODO: Se precisar especificar algum valor para atualizar a métrica, o parametro 'ignoredValue' deve ser usado. */
    public void updateClientMetric(ClientMetricType clientMetricType, String ignoredValue) {
        Validator.checkIsNotNull(clientMetricType, "clientMetricType");
        synchronized (ClientMetrics.class) {
            switch (clientMetricType) {
                case UDP_RECEIVED:
                    udpReceivedCount += 1;
                    break;
                case TCP_CONNECTION:
                    tcpConnectionCount += 1;
                    break;
            }
        }
    }

    public void updateClientMetric(ClientMetricType clientMetricType) {
        updateClientMetric(clientMetricType, null);
    }

    public int getTcpConnectionCount() {
        return tcpConnectionCount;
    }

    public int getUdpReceivedCount() {
        return udpReceivedCount;
    }
}
