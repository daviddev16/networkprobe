package com.networkprobe.core.util;

import com.networkprobe.core.config.model.Cidr;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.networkprobe.core.util.Validator.checkIsNotNull;

public final class Utility {

    public static @NotNull String readFile(File file) throws IOException {
        Validator.checkIsReadable(file, file.getName());
        return String.join("\n", Files.readAllLines(Paths.get(file.toURI()))).trim();
    }

    @Contract("_ -> new")
    public static @NotNull File toFile(String pathname) {
        return new File(Validator.checkIsNullOrEmpty(pathname, "pathname"));
    }

    @Contract("_ -> new")
    public static @NotNull JSONObject toJson(File file) throws IOException {
        return new JSONObject(readFile(file));
    }

    public static @NotNull String exceptionShortDescription(Exception exception) {
        checkIsNotNull(exception, "exception");
        return "[exception='" + exception.getClass().getSimpleName()
                + "', message='" + exception.getMessage() + "']";
    }

    public static @NotNull String sanitize(String str) {
        checkIsNotNull(str, "str");
        return str
                .replaceAll("[^a-zA-Z0-9_]", "") /* clear espcial chars */
                .replaceAll("\\s+", ""); /* remove blank spaces */
    }

    @Contract("_ -> new")
    public static @NotNull Cidr convertStringToCidr(String cidrNotation) {
        Validator.checkCidrNotation(cidrNotation);
        String[] parts = cidrNotation.split("/");
        String networkId = parts[0];
        String subnetMask = convertPrefixToMask(Integer.parseInt(parts[1]));
        return new Cidr(networkId, subnetMask);
    }

    public static @Nullable String convertPrefixToMask(int subnetPrefix) {
        Validator.checkBounds(subnetPrefix, 0, 32, "subnetPrefix");
        try {
            byte[] subnetMaskBytes = new byte[4];
            int bitsOfNetwork = 0xffffffff << (32 - subnetPrefix);
            for (int i = 0; i < 4; i++) {
                subnetMaskBytes[i] = (byte) ((bitsOfNetwork >> (24 - i * 8)) & 0xff);
            }
            InetAddress mascaraInet = InetAddress.getByAddress(subnetMaskBytes);
            return mascaraInet.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static int convertMaskToPrefix(String subnetMask) {
        Validator.checkIsAValidIpv4(subnetMask, "subnetMask");
        try {
            InetAddress subnetInetAddress = InetAddress.getByName(subnetMask);
            byte[] subnetMaskBytes = subnetInetAddress.getAddress();
            int prefix = 0;
            for (byte b : subnetMaskBytes) {
                for (int i = 7; i >= 0; i--) {
                    if (((b >> i) & 1) == 1) {
                        prefix++;
                    }
                }
            }
            return prefix;
        } catch (UnknownHostException e) {
            return -1;
        }
    }

    @Contract(pure = true)
    public static String clearIpv6Address(String address) {
        checkIsNotNull(address, "address");
        String[] divider = address.split("%");
        if (divider.length == 0)
            return address;
        return divider[0];
    }
}
