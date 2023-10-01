package com.networkprobe.core.util;

import com.networkprobe.core.model.CidrNotation;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static com.networkprobe.core.util.Validator.*;

public class Utility {

    public static final int MIN_BUFFER_SIZE = 2;

    public static void closeQuietly(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null)
                    closeable.close();
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> mapOf(Map<String, Object> parent, String keyOfMap) {
        return (Map<String, Object>) parent.get(keyOfMap);
    }

    public static boolean asBoolean(Object object, String fieldName) {
        return Boolean.parseBoolean( Validator.nonNull(object, fieldName).toString() );
    }

    public static int asInt(Object object) {
        return Integer.parseInt( Validator.nonNull(object, "object").toString() );
    }

    public static String asString(Object object) {
        return (object != null) ? object.toString() : null;
    }

    public static @NotNull String readFile(File file) throws IOException {
        checkIsReadable(file, file.getName());
        return String.join("\n", Files.readAllLines(Paths.get(file.toURI()))).trim();
    }

    public static DatagramPacket createABufferedPacket(int length) {
        byte[] buffer = new byte[ (length < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE : length ];
        return new DatagramPacket(buffer, 0, buffer.length);
    }

    public static InetAddress getInetAddress(int simplifiedAddress) throws UnknownHostException {
        byte[] addressArray = new byte[4];
        for (int i = 0; i < addressArray.length; i++) {
            addressArray[addressArray.length - i - 1] = (byte) (simplifiedAddress & 0xFF);
            simplifiedAddress >>= 8;
        }
        return InetAddress.getByAddress(addressArray);
    }

    public static int convertInetToInterger(InetAddress inetAddress) {
        int value = 0;
        byte[] addressArray = inetAddress.getAddress();
        for (byte b : addressArray) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    public static int convertSocketAddressToInterger(Socket socket) {
        return convertInetToInterger(socket.getInetAddress());
    }

    public static @NotNull CidrNotation convertStringToCidrNotation(String cidrNotation) {
        checkCidrNotation(cidrNotation);
        String[] parts = cidrNotation.split("/");
        String networkId = parts[0];
        String subnetMask = convertPrefixToMask(Integer.parseInt(parts[1]));
        return new CidrNotation(convertStringToByteArray(networkId),
                convertStringToByteArray(subnetMask));
    }

    public static byte @Nullable [] convertStringToByteArray(String address) {
        try {
            return InetAddress
                    .getByName(nonNull(address, "address"))
                    .getAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String convertByteArrayToString(byte[] networkOctets) {
        StringJoiner joiner = new StringJoiner(".");
        for (byte octet : networkOctets) {
            joiner.add(String.valueOf(octet & 0xFF));
        }
        return joiner.toString();
    }

    public static @Nullable String convertPrefixToMask(int subnetPrefix) {
        checkBounds(subnetPrefix, 0, 32, "subnetPrefix");
        try {
            byte[] subnetMaskBytes = new byte[4];
            int bitsOfNetwork = 0xffffffff << (32 - subnetPrefix);
            for (int i = 0; i < 4; i++) {
                subnetMaskBytes[i] = (byte) ((bitsOfNetwork >> (24 - i * 8)) & 0xff);
            }
            InetAddress subnetInet = InetAddress.getByAddress(subnetMaskBytes);
            return subnetInet.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static @NotNull <T> List<T> convertJsonArrayToList(@Nullable JSONArray jsonArray,
                                                              @NotNull Class<T> itemType) {
        nonNull(itemType, "itemType");
        List<T> newList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object jsonValue = jsonArray.get(i);
                if (jsonValue != null && jsonValue.getClass().isAssignableFrom(itemType))
                    newList.add((T) jsonValue);
            }
        }
        return newList;
    }

    public static int convertMaskToPrefix(String subnetMask) {
        checkIsAValidIpv4(subnetMask, "subnetMask");
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

    public static String clearIpv6Address(String address) {
        nonNull(address, "address");
        String[] divider = address.split("%");
        if (divider.length == 0)
            return address;
        return divider[0];
    }

}
