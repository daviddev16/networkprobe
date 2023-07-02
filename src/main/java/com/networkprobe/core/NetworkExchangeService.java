package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.api.SocketDataMessageProcessor;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.command.SocketCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Responsável por responder e transmitir os comandos da rede via TCP/IP. **/
@Singleton(creationType = SingletonType.LAZY)
public final class NetworkExchangeService extends ExecutionWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkExchangeService.class);
    private static final Template templateLoader = JsonTemplateAdapter.getTemplateInstance();
    private final SocketDataMessageProcessor processor = new SocketCommandProcessor();
    public static final int SERVER_PORT = 14477;

    private ServerSocket serverSocket;

    public NetworkExchangeService()
    {
        super("exchange-service", true, false);
        SingletonDirectory.denyInstantiation(NetworkExchangeService.class);
    }

    @Override
    public void onBegin() {
        try {
            String bindAddress = templateLoader.getNetworking().getTcpBindAddress();
            int tcpBacklog = templateLoader.getNetworking().getTcpSocketBacklog();
            InetAddress inetAddress = InetAddress.getByName(bindAddress);
            serverSocket = new ServerSocket(SERVER_PORT, tcpBacklog, inetAddress);
            LOGGER.info("Escutando na porta {} por requisições de comandos.", SERVER_PORT);
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOGGER, e, 156);
        }
    }

    @Override
    public void onUpdate() {
        try {
            synchronized (serverSocket) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (Exception e)
        {
            ExceptionHandler.unexpected(LOGGER, e, 166);
        }
    }

    @Override
    public void onStop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (Exception e) { /* ignore */ }
    }

    public static NetworkExchangeService getExchangeService() {
        return SingletonDirectory.getSingleOf(NetworkExchangeService.class);
    }

}
