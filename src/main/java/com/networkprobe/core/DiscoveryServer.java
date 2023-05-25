package com.networkprobe.core;

import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.exception.ExceptionHandler;
import com.networkprobe.core.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/** Responsável por responder a pacotes "HELLO" de UDP vindos da rede **/
public class DiscoveryServer extends NetworkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryServer.class);

    public static final int DISCOVERY_PORT = 14476;

    private DatagramSocket datagramSocket;

    public DiscoveryServer(TemplateLoader templateLoader) {
        super("broadcast-server", true, false, templateLoader);
    }

    @Override
    protected void onBegin() {
        try {
            String bindAddress = getTemplateLoader().getNetworking().getUdpBroadcastAddress();
            InetAddress inetAddress = InetAddress.getByName(bindAddress);
            datagramSocket = new DatagramSocket(new InetSocketAddress(inetAddress, DISCOVERY_PORT));
            LOGGER.info("Escutando na porta {} por tentativas de descoberta.", DISCOVERY_PORT);
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOGGER, e, 155);
        }
    }

    /*TODO:REFACTOR:*/
    private void process(DatagramPacket packet) throws IOException {

        String address = packet.getAddress().getHostAddress();
        String dataMessage = NetworkUtil.getBufferedData(packet);

        /*TODO:if (!Messages.checkMessage(dataMessage)) {
            LOGGER.warn("Unknown message data.");
        }
        else if (dataMessage.equals(Messages.HELLO)) {
            datagramSocket.send(Networking.createMessagePacket(packet.getAddress(),
                    packet.getPort(), Messages.HELLO));
        }*/
    }

    @Override
    public void onUpdate() {
        try {
            final DatagramPacket packet = NetworkUtil.createABufferedPacket(0);
            datagramSocket.receive(packet);
            process(packet);
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOGGER, e, 1);
        }
    }

    @Override
    public void onStop() {
        try {
            if (datagramSocket != null)
                datagramSocket.close();
        } catch (Exception e) { /* ignore */ }
    }

}
