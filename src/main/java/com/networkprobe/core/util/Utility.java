package com.networkprobe.core.util;

import com.networkprobe.core.config.model.Cidr;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Utility {

    public static String sanitize(String str) {
        return str
                .replaceAll("[^a-zA-Z0-9_]", "") /* clear espcial chars */
                .replaceAll("\\s+", ""); /* remove blank spaces */
    }

    public static boolean isFunction(String value) throws NullPointerException {
        Validator.checkIsNotNull(value, "value");
        return value.startsWith("func::") && (value.contains("(") && value.contains(")"));
    }

    public static String readFile(File file) throws IOException {
        Validator.checkIsReadable(file, file.getName());
        return String.join("\n", Files.readAllLines(Paths.get(file.toURI()))).trim();
    }

    public static File toFile(String pathname) {
        return new File(Validator.checkIsNullOrEmpty(pathname, "pathname"));
    }

    public static JSONObject toJson(File file) throws IOException {
        return new JSONObject(readFile(file));
    }

    public static Cidr convertStringToCidr(String cidrNotation) {
        Validator.checkCidrNotation(cidrNotation);
        String[] parts = cidrNotation.split("/");
        String networkId = parts[0];
        String subnetMask = convertPrefixToMask(Integer.parseInt(parts[1]));
        return new Cidr(networkId, subnetMask);
    }

    public static String convertPrefixToMask(int subnetPrefix) {
        Validator.checkBounds(subnetPrefix, 0, 32, "subnetPrefix");
        try {
            byte[] subnetMaskBytes = new byte[4];
            int bitsOfNetwork = 0xffffffff << (32 - subnetPrefix);
            for (int i = 0; i < 4; i++) {
                subnetMaskBytes[i] = (byte) ((bitsOfNetwork >> (24 - i * 8)) & 0xff);
            }
            InetAddress mascaraInet = InetAddress.getByAddress(subnetMaskBytes);
            String subnetMask = mascaraInet.getHostAddress();
            return subnetMask;
        } catch (UnknownHostException e) {
            return "?.?.?.?";
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

}
