package com.networkprobe.core;

import com.networkprobe.core.api.ResponseEntity;
import com.networkprobe.core.util.NetworkUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Map;

/**
 * MetricsResponseEntity é uma derivação de uma response que é usado
 * para monitoramento através do socket tcp
 * */
public class MetricsResponseEntity implements ResponseEntity<String> {

    @Override
    public String getContent() {
        JSONArray jsonArray = new JSONArray();
        Map<Integer, ClientMetrics> clientMetricsMap = NetworkMonitorService.getAllMetrics();
        try {
            for (Map.Entry<Integer, ClientMetrics> metricsEntry : clientMetricsMap.entrySet()) {
                InetAddress entryInetAddress = NetworkUtil.getInetAddress(metricsEntry.getKey());
                ClientMetrics clientMetrics = metricsEntry.getValue();
                JSONObject metricJsonObject = new JSONObject()
                        .put("inet_address", entryInetAddress.getHostAddress())
                        .put("tcp_connections", clientMetrics.getTcpConnectionCount())
                        .put("udp_connections", clientMetrics.getUdpReceivedCount());
                jsonArray.put(metricJsonObject);
            }
            return jsonArray.toString();
        } catch (Exception ignore) {
            return "Um erro ocorreu no processamento do comando";
        }
    }

    @Override
    public boolean isCachedOnce() {
        return false;
    }

    @Override
    public String getRawContent() {
        return null;
    }
}
