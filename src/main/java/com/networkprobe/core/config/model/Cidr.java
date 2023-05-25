package com.networkprobe.core.config.model;


import com.networkprobe.core.util.NetworkUtil;

public class Cidr {

    public static final Cidr NONE = new Cidr("255.255.255.255", "255.255.255.255");
    public static final Cidr ALL = new Cidr("0.0.0.0", "0.0.0.0");

    private final String networkId;
    private final String subnetMask;

    public Cidr(String networkId, String subnetMask) {
        this.networkId = networkId;
        this.subnetMask = subnetMask;
    }

    public String getNetworkId() {
        return networkId;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public int getSubnetPrefix() {
        return NetworkUtil.convertMaskToPrefix(subnetMask);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", getNetworkId(), getSubnetPrefix());
    }
}
