package com.networkprobe.core.networking;

import com.networkprobe.core.annotation.Address;
import com.networkprobe.core.annotation.Hide;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.Enumeration;

import static com.networkprobe.core.util.Utility.*;

@Address
public final class MappedNetworkFunctions {

    public MappedNetworkFunctions() {}

    @Nullable
    public String getAddressOf(String interfaceName, String inetType, int index) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while ((interfaces.hasMoreElements())) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.getName().equalsIgnoreCase(interfaceName)) {
                return getInetAddressOfInterface(ni, inetType, index);
            }
        }
        return null;
    }

    @Hide
    @Nullable
    private String getInetAddressOfInterface(NetworkInterface networkInterface, String inetType, int index) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        int currentIndex = 0;
        while (inetAddresses.hasMoreElements()) {
            InetAddress currentInetAddress = inetAddresses.nextElement();
            if (inetType.equalsIgnoreCase("ipv4") &&
                    currentInetAddress instanceof Inet4Address) {
                if (currentIndex == index)
                    return currentInetAddress.getHostAddress();
                currentIndex++;
            }
            else if (inetType.equalsIgnoreCase("ipv6") &&
                    currentInetAddress instanceof Inet6Address) {
                if (currentIndex == index)
                    return clearIpv6Address(currentInetAddress.getHostAddress());
                currentIndex++;
            }
        }
        return null;
    }

}
