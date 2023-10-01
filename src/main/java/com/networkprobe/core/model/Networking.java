package com.networkprobe.core.model;

import static com.networkprobe.core.util.Validator.*;

public class Networking implements Cloneable {

    private String tcpBindAddress;
    private String udpBroadcastAddress;
    private int udpRequestThreshold;
    private int tcpSocketBacklog;
    private int tcpConnectionThreshold;
    private boolean enableDiscovery;

    private Networking(String tcpBindAddress, String udpBroadcastAddress, int udpRequestThreshold,
                       int tcpSocketBacklog, boolean enableDiscovery, int tcpConnectionThreshold) {
        this.tcpBindAddress = tcpBindAddress;
        this.udpBroadcastAddress = udpBroadcastAddress;
        this.udpRequestThreshold = udpRequestThreshold;
        this.tcpSocketBacklog = tcpSocketBacklog;
        this.enableDiscovery = enableDiscovery;
        this.tcpConnectionThreshold = tcpConnectionThreshold;
    }

    private Networking() {}

    public int getTcpConnectionThreshold() {
        return tcpConnectionThreshold;
    }

    public void setTcpConnectionThreshold(int tcpConnectionThreshold) {
        this.tcpConnectionThreshold = tcpConnectionThreshold;
    }

    public String getTcpBindAddress() {
        return tcpBindAddress;
    }

    private void setTcpBindAddress(String tcpBindAddress) {
        this.tcpBindAddress = tcpBindAddress;
    }

    public String getUdpBroadcastAddress() {
        return udpBroadcastAddress;
    }

    public void setUdpBroadcastAddress(String udpBroadcastAddress) {
        this.udpBroadcastAddress = udpBroadcastAddress;
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

        public Builder tcpBindAddress(String tcpBindAddress) {
            checkIsAValidIpv4(tcpBindAddress, "tcpBindAddress");
            networking.setTcpBindAddress(tcpBindAddress);
            return this;
        }

        public Builder udpBroadcastAddress(String udpBroadcastAddress) {
            checkIsAValidIpv4(udpBroadcastAddress, "udpBroadcastAddress");
            networking.setUdpBroadcastAddress(udpBroadcastAddress);
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

        public Builder tcpConnectionThreshold(int tcpConnectionThreshold) {
            checkIsPositive(tcpConnectionThreshold, "tcpConnectionThreshold");
            checkIsLowerThan(tcpConnectionThreshold, 50, "tcpConnectionThreshold");
            networking.setTcpConnectionThreshold(tcpConnectionThreshold);
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
