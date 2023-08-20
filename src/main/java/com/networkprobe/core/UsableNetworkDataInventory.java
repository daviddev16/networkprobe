package com.networkprobe.core;

import com.networkprobe.core.annotation.AddressAsInventory;
import com.networkprobe.core.annotation.Internal;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.util.Utility;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.Enumeration;

/**
 * Essa classe é usada como referência para comandos que precisam de uma resposta dinâmica,
 * é onde fica o mapa de todas as funções endereçáveis nos comandos
 * */

@AddressAsInventory
@Singleton(creationType = SingletonType.DYNAMIC, order = -450)
public final class UsableNetworkDataInventory {

    public UsableNetworkDataInventory()
    {
        SingletonDirectory.denyInstantiation(this);
    }

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

    @Internal
    @Nullable
    private String getInetAddressOfInterface(NetworkInterface networkInterface, String inetAddressType, int index) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        int currentIndex = 0;

        while (inetAddresses.hasMoreElements()) {
            InetAddress currentInetAddress = inetAddresses.nextElement();
            if (inetAddressType.equalsIgnoreCase("ipv4") &&
                    currentInetAddress instanceof Inet4Address) {
                if (currentIndex == index) {
                    return currentInetAddress.getHostAddress();
                }
                currentIndex++;
            }
            else if (inetAddressType.equalsIgnoreCase("ipv6") &&
                    currentInetAddress instanceof Inet6Address) {
                if (currentIndex == index) {
                    return Utility.clearIpv6Address(currentInetAddress.getHostAddress());
                }
                currentIndex++;
            }
        }
        return null;
    }

    @Internal
    private String toMacAddress(byte[] hardwareAddress) {
        StringBuilder builder = new StringBuilder();
        for (byte address : hardwareAddress) {
            builder.append(String.format("%02x", address)).append(' ');
        }
        return builder.toString().trim();
    }

    @Internal
    public static UsableNetworkDataInventory getInventory() {
        return SingletonDirectory.getSingleOf(UsableNetworkDataInventory.class);
    }
}
