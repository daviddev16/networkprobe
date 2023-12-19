package com.networkprobe.core;

import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.statistics.Metric;
import com.networkprobe.core.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Responsável por responder e transmitir os comandos da rede via TCP/IP. **/
@Singleton(creationType = SingletonType.DYNAMIC)
public final class NetworkExchangeService extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkExchangeService.class);
    public static final int SERVER_PORT = 14477;

    @Handled
    private NetworkMonitorService monitorService;

    @Handled
    private Template template;

    private ServerSocket serverSocket;

    public NetworkExchangeService() {
        super("exchange-service", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    public void onBegin() {
        try {

            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(
                    template.getNetworking().getTcpBindAddress(), SERVER_PORT),
                    template.getNetworking().getTcpSocketBacklog());
            LOG.info("Escutando na porta {} por requisições de comandos.", SERVER_PORT);

        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.TCP_SOCKET_BEGIN);
        }

    }

    @Override
    public void onUpdate() {
        try {

            synchronized (serverSocket) {

                Socket clientSocket = serverSocket.accept();
                ClientMetrics clientMetrics = monitorService.getMetrics(clientSocket.getInetAddress());

                if (allowTcpConnection(clientMetrics)) {
                    clientMetrics.upgradeMetricValue(Metric.TCP_ACCEPTED_COUNT);
                    ClientHandler.delegateHandlerTo(clientSocket, clientMetrics);
                }
                else if (NetworkProbeOptions.isDebugSocketEnabled()) {
                    LOG.debug("'{}' ultrapassou o limite de conexões configurado.", clientSocket
                            .getInetAddress().getHostAddress());
                    Utility.closeQuietly(clientSocket);
                }

            }

        } catch (Exception e) {
            ExceptionHandler.handleUnexpected(LOG, e, Reason.TCP_SOCKET_UPDATE);
        }

    }

    public boolean allowTcpConnection(ClientMetrics clientMetrics) {
        return clientMetrics.getIntegerValueOf(Metric.TCP_ACCEPTED_COUNT) < template.getNetworking()
                .getTcpConnectionThreshold();
    }

    public static NetworkExchangeService getExchangeService() {
        return SingletonDirectory.getSingleOf(NetworkExchangeService.class);
    }

    @Override
    public void onStop() {
        Utility.closeQuietly(serverSocket);
    }

}
