package com.networkprobe.core;

import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Responsável por responder e transmitir os comandos da rede via TCP/IP. **/
public final class ExchangeServer extends NetworkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeServer.class);
    public static final int SERVER_PORT = 14477;

    private ServerSocket serverSocket;

    public ExchangeServer(TemplateLoader templateLoader) {
        super("exchange-server", true, false, templateLoader);
    }

    @Override
    public void onBegin() {
        try {
            String bindAddress = getTemplateLoader().getNetworking().getTcpBindAddress();
            int tcpBacklog = getTemplateLoader().getNetworking().getTcpSocketBacklog();
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
                ClientServerHandler clientHandler = new ClientServerHandler(clientSocket);
                clientHandler.start();
            }
        } catch (Exception e) {
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
}
