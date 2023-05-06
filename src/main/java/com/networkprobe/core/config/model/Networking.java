package com.networkprobe.core.config.model;

import static com.networkprobe.core.util.Validator.*;

public class Networking implements Cloneable {

    private String bindAddress;
    private int udpRequestThreshold;
    private int tcpSocketBacklog;
    private boolean enableDiscovery;

    private Networking(String bindAddress, int udpRequestThreshold, int tcpSocketBacklog, boolean enableDiscovery) {
        this.bindAddress = bindAddress;
        this.udpRequestThreshold = udpRequestThreshold;
        this.tcpSocketBacklog = tcpSocketBacklog;
        this.enableDiscovery = enableDiscovery;
    }

    private Networking() {}

    public String getBindAddress() {
        return bindAddress;
    }

    private void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public int getUdpRequestThreshold() {
        return udpRequestThreshold;
    }

    private void setUdpRequestThreshold(int udpRequestThreshold) {
        this.udpRequestThreshold = udpRequestThreshold;
    }

    public int getTcpSocketBacklog() {
        return tcpSocketBacklog;
    }

    private void setTcpSocketBacklog(int tcpSocketBacklog) {
        this.tcpSocketBacklog = tcpSocketBacklog;
    }

    public boolean isDiscoveryEnabled() {
        return enableDiscovery;
    }

    private void setEnableDiscovery(boolean enableDiscovery) {
        this.enableDiscovery = enableDiscovery;
    }

    public static final class Builder {

        private final Networking networking = new Networking();

        public Builder bindAddress(String bindAddress) {
            checkIsAValidIpv4(bindAddress, "bindAddress");
            networking.setBindAddress(bindAddress);
            return this;
        }

        public Builder udpRequestThreshold(int udpRequestThreshold) {
            checkIsPositive(udpRequestThreshold, "udpRequestThreshold");
            checkIsLowerThan(udpRequestThreshold, 6, "udpRequestThreshold");
            networking.setUdpRequestThreshold(udpRequestThreshold);
            return this;
        }

        public Builder tcpSocketBacklog(int tcpSocketBacklog) {
            checkIsPositive(tcpSocketBacklog, "tcpSocketBacklog");
            checkIsLowerThan(tcpSocketBacklog, 50, "tcpSocketBacklog");
            networking.setTcpSocketBacklog(tcpSocketBacklog);
            return this;
        }

        public Builder enableDiscovery(boolean enableDiscovery) {
            networking.setEnableDiscovery(enableDiscovery);
            return this;
        }

        public Networking get() {
            return networking;
        }
    }
}
