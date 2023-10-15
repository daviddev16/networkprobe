package com.networkprobe.core;

import com.networkprobe.core.annotation.AddressAsInventory;
import com.networkprobe.core.annotation.Internal;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.annotation.UseAsData;
import com.networkprobe.core.util.Utility;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.networkprobe.core.util.Utility.getMacAddress;

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

    @UseAsData(name = "GetActiveAddress")
    public String getActiveAddress(String macAddress, int addressIndex, String addressInetType) {

        List<String> addresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] macAddressArray = networkInterface.getHardwareAddress();

                if (macAddressArray == null || !getMacAddress(macAddressArray)
                        .equalsIgnoreCase(macAddress))
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
    public static UsableNetworkDataInventory getInventory() {
        return SingletonDirectory.getSingleOf(UsableNetworkDataInventory.class);
    }
}
