package com.networkprobe.core;

import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Responsável por responder e transmitir os comandos da rede via TCP/IP. **/
@Singleton(creationType = SingletonType.LAZY)
public final class NetworkExchangeService extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkExchangeService.class);
    public static final int SERVER_PORT = 14477;

    private ServerSocket serverSocket;

    @ManagedDependency
    private NetworkMonitorService monitorService;

    public NetworkExchangeService()
    {
        super("exchange-service", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    public void onBegin() {
        try {
            Template template = JsonTemplateAdapter.getTemplateInstance();
            serverSocket = new ServerSocket(SERVER_PORT, template.getNetworking().getTcpSocketBacklog(),
                    InetAddress.getByName(template.getNetworking().getTcpBindAddress()));
            LOG.info("Escutando na porta {} por requisições de comandos.", SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.unexpected(LOG, e, 156);
        }
    }

    @Override
    public void onUpdate() {
        try {
            synchronized (serverSocket) {
                Socket clientSocket = serverSocket.accept();
                int simplifiedAddress = NetworkUtil.getSimplifiedAddress(clientSocket.getInetAddress());
                ClientMetrics clientMetrics = monitorService.getMetrics(simplifiedAddress);
                if (allowTcpConnection(clientMetrics)) {
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();
                } else if (NetworkProbeOptions.isDebugSocketEnabled()) {
                    LOG.debug("'{}' ultrapassou o limite de conexões configurado.", clientSocket
                            .getInetAddress().getHostAddress());
                }
            }
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOG, e, 166);
        }
    }

    @Override
    public void onStop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (Exception e) { /* ignore */ }
    }

    public boolean allowTcpConnection(ClientMetrics clientMetrics) {
        return clientMetrics.getTcpConnectionCount() < JsonTemplateAdapter.getTemplateInstance()
                .getNetworking().getTcpConnectionThreshold();
    }

    public static NetworkExchangeService getExchangeService() {
        return SingletonDirectory.getSingleOf(NetworkExchangeService.class);
    }

}
