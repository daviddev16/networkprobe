package com.networkprobe.core.entity.internal;

import com.networkprobe.core.NetworkProbeOptions;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.miscs.Feature;
import com.networkprobe.core.annotation.reflections.CommandEntity;
import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.statistics.Metric;
import com.networkprobe.core.util.Utility;
import com.networkprobe.core.NetworkMonitorService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MetricsResponseEntity é uma derivação de uma response que é usado
 * para monitoramento através do socket tcp
 * */
@CommandEntity(commandName = "metrics")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
@Feature(notImplemented = false)
public class MetricsResponseEntity implements ResponseEntity<String> {

    @Handled
    private NetworkMonitorService monitorService;

    @Override
    public String getContent(List<String> arguments) {
        JSONArray jsonArray = new JSONArray();
        Map<Integer, ClientMetrics> clientMetricsMap = monitorService.getMapMetrics();
        try {
            for (ClientMetrics clientMetrics : clientMetricsMap.values()) {
                JSONObject clientMetricsJsonValue = new JSONObject();
                clientMetrics.forEachMetric((metric, value) ->
                        clientMetricsJsonValue.put(metric.name(), value));
                jsonArray.put(clientMetricsJsonValue);
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
