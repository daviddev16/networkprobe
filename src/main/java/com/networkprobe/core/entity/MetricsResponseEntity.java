package com.networkprobe.core.entity;

import com.networkprobe.core.NetworkMonitorService;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.CommandEntity;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.statistics.Metric;
import com.networkprobe.core.util.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * MetricsResponseEntity é uma derivação de uma response que é usado
 * para monitoramento através do socket tcp
 * */
@CommandEntity(commandName = "metrics")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
public class MetricsResponseEntity implements ResponseEntity<String> {

    @ManagedDependency
    private NetworkMonitorService monitorService;

    /* TODO: mover implementação para NetworkServicesFacade */

    @Override
    public String getContent(List<String> arguments) {
        JSONArray jsonArray = new JSONArray();
        Map<Integer, ClientMetrics> clientMetricsMap = monitorService.getMetrics();
        try {
            for (Map.Entry<Integer, ClientMetrics> metricsEntry : clientMetricsMap.entrySet()) {
                InetAddress entryInetAddress = Utility.getInetAddress(metricsEntry.getKey());
                ClientMetrics clientMetrics = metricsEntry.getValue();
                JSONObject metricJsonObject = new JSONObject()
                        .put("inet_address", entryInetAddress.getHostAddress())
                        .put("tcp_connections", clientMetrics.get(Metric.TCP_ACCEPTED_COUNT))
                        .put("udp_connections", clientMetrics.get(Metric.UDP_RECEIVED_COUNT))
                        .put("latest_requested_commands", "...");
                jsonArray.put(metricJsonObject);
            }
            return jsonArray.toString();
        } catch (Exception exception) {
            /*TODO:*/
            return exception.getMessage();
        }
    }

    @Override
    public boolean isCachedOnce() {
        return false;
    }

    @Override
    public String getRawContent() {
        return "";
    }

}
