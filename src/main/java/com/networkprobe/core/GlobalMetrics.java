package com.networkprobe.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlobalMetrics {

    private static Logger LOG = LoggerFactory.getLogger(GlobalMetrics.class);

    private static volatile int totalUdpConnections = 0;
    private static volatile int totalTcpConnections = 0;
    private static volatile long startTime = System.currentTimeMillis();

    public static void stopMetrics() {
        LOG.info("Total UDP connections: " + totalUdpConnections);
        LOG.info("Total TCP connections: " + totalTcpConnections);
        LOG.info("Running time: " + ((System.currentTimeMillis() - startTime)/1000)  +"s");
    }

    public static void updatTcpMetric() {
        totalTcpConnections+=1;
    }

    public static void updatUdpMetric() {
        totalUdpConnections+=1;
    }

}
