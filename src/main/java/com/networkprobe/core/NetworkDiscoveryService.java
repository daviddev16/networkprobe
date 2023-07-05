package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Responsável por responder a pacotes "HELLO" de UDP vindos da rede **/

@Singleton(creationType = SingletonType.LAZY)
public class NetworkDiscoveryService extends ExecutionWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkDiscoveryService.class);
    private static final Template template = JsonTemplateAdapter.getTemplateInstance();

    public static final int DISCOVERY_PORT = 14476;
    public static final byte[] HELLO_FLAG = "H".getBytes(StandardCharsets.UTF_8);

    private DatagramSocket datagramSocket;

    public NetworkDiscoveryService()
    {
        super("discovery-service", true, false);
        SingletonDirectory.denyInstantiation(this);
    }

    @Override
    protected void onBegin() {
        try {

            String bindAddress = template.getNetworking().getUdpBroadcastAddress();
            datagramSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName(bindAddress), DISCOVERY_PORT));
            LOGGER.info("Escutando na porta {} por tentativas de descoberta.", DISCOVERY_PORT);

        } catch (Exception e)
        {
            ExceptionHandler.unexpected(LOGGER, e, 155);
        }
    }

    @Override
    public void onUpdate() {
        try {
            final DatagramPacket packet = NetworkUtil.createABufferedPacket(0);
            datagramSocket.receive(packet);
            onReceivedPacket(packet);
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOGGER, e, 1);
        }
    }

    private void onReceivedPacket(DatagramPacket packet) throws IOException
    {
        if (packet.getData()[0] == HELLO_FLAG[0])
            datagramSocket.send(new DatagramPacket(HELLO_FLAG, 0, HELLO_FLAG.length,
                    packet.getAddress(), packet.getPort()));
        else
            LOGGER.warn("UNKNOWN FLAG RECEIVED");
    }

    @Override
    public void onStop() {
        try {
            if (datagramSocket != null)
                datagramSocket.close();
        } catch (Exception e) { /* ignore */ }
    }

    public static NetworkDiscoveryService getDiscoveryService() {
        return SingletonDirectory.getSingleOf(NetworkDiscoveryService.class);
    }

}
