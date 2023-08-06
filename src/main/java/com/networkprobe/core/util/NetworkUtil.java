package com.networkprobe.core.util;

import com.networkprobe.core.config.CidrNotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.networkprobe.core.util.Validator.checkIsNotNull;

public class NetworkUtil {

	public static final int MIN_BUFFER_SIZE = 2;
	public static final String ALL_INTERFACES_BROADCAST_ADDRESS = "255.255.255.255";

	public static NetworkInterface getNetworkInterfaceByAddress(String addressString)
			throws UnknownHostException, SocketException {
		Validator.checkIsAValidIpv4(addressString, "addressString");
		InetAddress address = InetAddress.getByName(addressString);
		return NetworkInterface.getByInetAddress(address);
	}

	public static Set<String> getBroadcastAddresses(NetworkInterface networkInterface) {
		Validator.checkIsNotNull(networkInterface, "networkInterface");
		Set<String> broadcastAddresses = new LinkedHashSet<>();
		for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
			if (interfaceAddress.getBroadcast() != null)
				broadcastAddresses.add(interfaceAddress.getBroadcast().getHostAddress());
		}
		if (broadcastAddresses.isEmpty())
			broadcastAddresses.add(ALL_INTERFACES_BROADCAST_ADDRESS);
		return broadcastAddresses;
	}

	public static String getFirstBroadcast(Set<String> broadcastAddresses) {
		Validator.checkIsNotNull(broadcastAddresses, "broadcastAddresses");
		return broadcastAddresses.stream()
				.findFirst()
				.orElse(null);
	}

	public static DatagramPacket createMessagePacket(InetAddress inetAddress, int port, String message) {
		Validator.checkIsNotNull(inetAddress, "inetAddress");
		Validator.checkIsNotNull(message, "message");
		byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
		return new DatagramPacket(buffer, 0, buffer.length, inetAddress, port);
	}

	public static DatagramPacket createABufferedPacket(int length) {
		byte[] buffer = new byte[ (length < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE : length ];
		return new DatagramPacket(buffer, 0, buffer.length);
	}

	public static String getBufferedData(DatagramPacket packet) {
		Validator.checkIsNotNull(packet, "packet");
		return new String(packet.getData(), StandardCharsets.UTF_8).trim();
	}

	public static InetAddress getInetAddress(int simplifiedAddress) throws UnknownHostException {
		byte[] addressArray = new byte[4];
		for (int i = 0; i < addressArray.length; i++) {
			addressArray[addressArray.length - i - 1] = (byte) (simplifiedAddress & 0xFF);
			simplifiedAddress >>= 8;
		}
		return InetAddress.getByAddress(addressArray);
	}

	public static int getSimplifiedAddress(InetAddress inetAddress) {
		int value = 0;
		byte[] addressArray = inetAddress.getAddress();
		for (int i = 0; i < addressArray.length; i++) {
			value = (value << 8) | (addressArray[i] & 0xFF);
		}
		/*
		 * Fazer teste de performance como:
		 * ByteBuffer.wrap(inetAddress.getAddress()).getInt();
		*/
		return value;
	}

	public static @NotNull CidrNotation convertStringToCidrNotation(String cidrNotation) {
		Validator.checkCidrNotation(cidrNotation);
		String[] parts = cidrNotation.split("/");
		String networkId = parts[0];
		String subnetMask = convertPrefixToMask(Integer.parseInt(parts[1]));
		/*  usar o builder */
		return new CidrNotation(convertStringToByteArray(networkId),
				convertStringToByteArray(subnetMask));
	}

	@Nullable
	public static byte[] convertStringToByteArray(String address) {
		Validator.checkIsNotNull(address, "address");
		try {
			return InetAddress.getByName(address).getAddress();
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

	public static String clearIpv6Address(String address) {
		checkIsNotNull(address, "address");
		String[] divider = address.split("%");
		if (divider.length == 0)
			return address;
		return divider[0];
	}

	public static void closeQuietly(Socket socket) {
		try {
			if (socket != null)
				socket.close();
		} catch (Exception ignored) {/*  */}
	}

}
