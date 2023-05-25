package com.networkprobe.core.networking;

import com.networkprobe.core.annotation.Address;
import com.networkprobe.core.annotation.Hide;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.Enumeration;

import static com.networkprobe.core.util.NetworkUtil.*;

/**
 * Essa classe é usada como referência para comandos que precisam de uma resposta dinâmica,
 * é onde fica o mapa de todas as funções endereçáveis nos comandos
 * */

@Address
public final class MappedNetworkFunctions {

    public static final MappedNetworkFunctions COMMON = new MappedNetworkFunctions();

    public MappedNetworkFunctions() {}

    @Nullable
    public String getAddressOf(String interfaceName, String inetAddressType, int index) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while ((interfaces.hasMoreElements())) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.getName().equalsIgnoreCase(interfaceName)) {
                return getInetAddressOfInterface(ni, inetAddressType, index);
            }
        }
        return null;
    }

    @Hide
    @Nullable
    private String getInetAddressOfInterface(NetworkInterface networkInterface, String inetAddressType, int index) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        int currentIndex = 0;
        while (inetAddresses.hasMoreElements()) {
            InetAddress currentInetAddress = inetAddresses.nextElement();
            if (inetAddressType.equalsIgnoreCase("ipv4") &&
                    currentInetAddress instanceof Inet4Address) {
                if (currentIndex == index)
                    return currentInetAddress.getHostAddress();
                currentIndex++;
            }
            else if (inetAddressType.equalsIgnoreCase("ipv6") &&
                    currentInetAddress instanceof Inet6Address) {
                if (currentIndex == index)
                    return clearIpv6Address(currentInetAddress.getHostAddress());
                currentIndex++;
            }
        }
        return null;
    }

}
