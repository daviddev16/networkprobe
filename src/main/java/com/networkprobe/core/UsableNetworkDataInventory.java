package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.ClassInventory;
import com.networkprobe.core.annotation.reflections.Data;
import com.networkprobe.core.annotation.reflections.Internal;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.util.Utility;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Essa classe é usada como referência para comandos que precisam de uma resposta dinâmica,
 * é onde fica o mapa de todas as funções endereçáveis nos comandos
 * */

@ClassInventory
@Singleton(creationType = SingletonType.DYNAMIC, order = -450)
@Documented(done = false)
public final class UsableNetworkDataInventory {

    public UsableNetworkDataInventory()
    {
        SingletonDirectory.denyInstantiation(this);
    }

    @Data(name = "GetActiveAddress")
    public String getActiveAddress(String macAddress, int addressIndex, String addressInetType) {

        List<String> addresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if (checkMACAddress(networkInterface, macAddress))
                    continue;

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {

                    InetAddress inetAddress = inetAddresses.nextElement();

                    switch (addressInetType) {
                        case "IPV4":
                            if (inetAddress instanceof Inet4Address)
                                addresses.add(inetAddress.getHostAddress());
                            break;
                        case "IPV6":
                            if (inetAddress instanceof Inet6Address)
                                addresses.add(inetAddress.getHostAddress());
                            break;
                    }

                }

            }
        } catch (Exception e) {
            return "Network Interface Error";
        }

        if (addresses.size() < addressIndex)
            return "InetAddress Unavailable";

        return addresses.get(addressIndex);
    }

    @Internal
    private boolean checkMACAddress(NetworkInterface networkInterface,
                                    String expectedMacAddress) throws SocketException {
        final byte[] macAddressArray = networkInterface.getHardwareAddress();
        return macAddressArray != null && Utility.getMacAddress(macAddressArray)
                .equalsIgnoreCase(expectedMacAddress);
    }

    @Internal
    public static UsableNetworkDataInventory getInventory() {
        return SingletonDirectory.getSingleOf(UsableNetworkDataInventory.class);
    }
}
