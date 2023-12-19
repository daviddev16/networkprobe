package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.statistics.Metric;
import com.networkprobe.core.util.Utility;
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

    @Handled
    private Template template;

    @Handled
    private NetworkMonitorService monitorService;

    private DatagramSocket datagramSocket;

    public NetworkDiscoveryService() {
        super("discovery-service", true, false);
        SingletonDirectory.denyInstantiation(this);
    }

    @Override
    protected void onBegin() {
        try {

            datagramSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName(template
                    .getNetworking().getUdpBroadcastAddress()), DISCOVERY_PORT));
            datagramSocket.setBroadcast(true);

            LOG.info("Escutando na porta {} por tentativas de descoberta.", DISCOVERY_PORT);

        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.UDP_SOCKET_BEGIN);
        }
    }

    @Override
    public void onUpdate() {
        try {

            /* dummyPacket é um packet com o buffer de tamanho 2 bytes */
            DatagramPacket dummyPacket = Utility.createABufferedPacket(0);
            datagramSocket.receive(dummyPacket);

            ClientMetrics metrics = monitorService.getMetrics(dummyPacket.getAddress());

            if (metrics.checkThresholdAndUpdate(Metric.UDP_RECEIVED_COUNT, template
                    .getNetworking().getUdpRequestThreshold()))
            {
                sendFeedbackHello(dummyPacket);
            }
            else if (NetworkProbeOptions.isDebugSocketEnabled())
                LOG.debug("A flag 'HELLO' foi bloqueada de ser respondida " +
                        "para o endereço: {}.", dummyPacket.getAddress().getHostAddress());

        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.UDP_SOCKET_UPDATE);
        }
    }

    private void sendFeedbackHello(DatagramPacket packet) throws IOException {
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

    public static NetworkDiscoveryService getDiscoveryService() {
        return SingletonDirectory.getSingleOf(NetworkDiscoveryService.class);
    }

    @Override
    public void onStop() {
        Utility.closeQuietly(datagramSocket);
    }

}
