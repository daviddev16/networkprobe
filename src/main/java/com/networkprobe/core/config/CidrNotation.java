package com.networkprobe.core.config;

import com.networkprobe.core.util.Utility;

public class CidrNotation {

    public static final CidrNotation ALL = new CidrNotation("0.0.0.0", "0.0.0.0");
    public static final CidrNotation NONE = new CidrNotation("255.255.255.255", "255.255.255.255");

    private final byte[] network;
    private final byte[] subnetMask;

    public CidrNotation(String network, String subnetMask) {
        this.network = Utility.convertStringToByteArray(network);
        this.subnetMask = Utility.convertStringToByteArray(subnetMask);
    }

    public CidrNotation(byte[] network, byte[] subnetMask) {
        this.network = network;
        this.subnetMask = subnetMask;
    }

    public byte[] getNetwork() {
        return network;
    }

    public byte[] getSubnetMask() {
        return subnetMask;
    }

    @Override
    public String toString() {
        return Utility.convertByteArrayToString(network) + "/" + Utility.convertMaskToPrefix
                (Utility.convertByteArrayToString(subnetMask));
    }
}
