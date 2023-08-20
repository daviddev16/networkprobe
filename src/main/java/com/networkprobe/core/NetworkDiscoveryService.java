package com.networkprobe.core;

import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Responsável por responder a pacotes "HELLO" de UDP vindos da rede **/

@Singleton(creationType = SingletonType.LAZY)
public class NetworkDiscoveryService extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkDiscoveryService.class);
    public static final byte[] HELLO_FLAG = "H".getBytes(StandardCharsets.UTF_8);
    public static final int DISCOVERY_PORT = 14476;

    @ManagedDependency private Template template;
    @ManagedDependency private NetworkMonitorService monitorService;

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
            datagramSocket.setBroadcast(true);
            LOG.info("Escutando na porta {} por tentativas de descoberta.", DISCOVERY_PORT);
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOG, e, 155);
        }
    }

    @Override
    public void onUpdate() {
        try {
            DatagramPacket bufferedPacket = NetworkUtil.createABufferedPacket(0);
            datagramSocket.receive(bufferedPacket);

            int addressIntegerValue = NetworkUtil.getValueFromAddress(bufferedPacket.getAddress());
            ClientMetrics clientMetrics = monitorService.getMetrics(addressIntegerValue);

            if (clientMetrics.getUdpReceivedCount() < template.getNetworking().getUdpRequestThreshold())
                sendHelloToClient(bufferedPacket);

            else if (NetworkProbeOptions.isDebugSocketEnabled()) {
                LOG.debug("A flag 'HELLO' foi bloqueada de ser respondida " +
                        "para o endereço: {}.", bufferedPacket.getAddress().getHostAddress());
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.unexpected(LOG, e, 1);
        }
    }

    private void sendHelloToClient(DatagramPacket packet) throws IOException {
        if (packet.getData()[0] == HELLO_FLAG[0]) {
            if (NetworkProbeOptions.isDebugSocketEnabled())
                LOG.debug("\"{}\" enviou uma flag de descoberta e será respondido.",
                        packet.getAddress().getHostAddress());
            datagramSocket.send(new DatagramPacket(HELLO_FLAG, 0, HELLO_FLAG.length,
                    packet.getAddress(), packet.getPort()));
        } else {
            if (NetworkProbeOptions.isDebugSocketEnabled()) {
                LOG.debug("\"{}\" enviou um datagrama não reconhecido.",
                        packet.getAddress().getHostAddress());
            }
            LOG.warn("UNKNOWN FLAG RECEIVED");
        }
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
