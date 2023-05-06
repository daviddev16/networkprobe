package com.networkprobe.core.networking;

import com.networkprobe.core.annotation.Addressable;
import com.networkprobe.core.annotation.NotAllowed;

@Addressable
public final class NetworkAddressableMethods {

    public NetworkAddressableMethods() {}

    public String getAddressOf(String interfaceName, String inetType, int index) {
        return "192.168.1.2";
    }

    @NotAllowed
    public String getDefaultPostgresPort() {
        return "5432";
    }

}
