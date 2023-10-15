package com.networkprobe.core;

import com.networkprobe.core.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.networkprobe.core.util.Utility.*;

public class ClientHandler extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
    private static final Set<Integer> connectedClients = new HashSet<>();
    private static final AtomicInteger ID = new AtomicInteger(-1);
    private static final SocketDataMessageProcessor messageProcessor;
    static
    {
        messageProcessor = (SocketDataMessageProcessor)
                SingletonDirectory.getBasedDependency(SocketDataMessageProcessor.class);
    }

    private final Socket clientSocket;

    private ClientHandler(Socket clientSocket) {
        super("client-worker" + ID.incrementAndGet(), false, false);
        this.clientSocket = clientSocket;
    }

    @Override
    public void onBegin() {
        PrintWriter outputWriter = null;
        Scanner inputScanner = null;
        try {
            register(clientSocket);
            outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            inputScanner = new Scanner(clientSocket.getInputStream());
            while (inputScanner.hasNextLine()) {
                String clientSentValue = inputScanner.nextLine();
                long start = System.currentTimeMillis();
                String response = messageProcessor.processSocketMessage(clientSentValue, clientSocket);
                System.out.println("Processed message took " + (System.currentTimeMillis() - start) + " ms");
                outputWriter.println(response);
                outputWriter.flush();
            }
        } catch (IOException exception) {
            ExceptionHandler.handleUnexpected(LOG, exception, Reason.TCP_CLIENT_HANDLER_PROCESS);
        } finally {
            closeQuietly(clientSocket, outputWriter, inputScanner);
            unregister(clientSocket);
        }
    }

    @Override
    protected void onUpdate() {
        /* o processamento dos comandos deve ser feito no onBegin, caso chegue
        no onUpdate a conexão deve ser forçada a ser encerrada */
        if (!clientSocket.isClosed()) {
                Utility.closeQuietly(clientSocket);
                unregister(clientSocket);
                stop();
        }
    }

    public static void delegateHandlerTo(Socket clientSocket) {

        if (!isAlreadyConnected(clientSocket)) {
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clientHandler.start();

        } else if (NetworkProbeOptions.isDebugSocketEnabled())
            LOG.debug("{} já está conectado em outra porta remota.", clientSocket
                    .getInetAddress().getHostAddress());

    }

    private static void register(Socket clientSocket) {
        connectedClients.add(convertSocketAddressToInterger(clientSocket));
    }

    private static void unregister(Socket clientSocket) {
        connectedClients.remove(convertSocketAddressToInterger(clientSocket));
    }

    private static boolean isAlreadyConnected(Socket clientSocket) {
        return connectedClients.contains(convertSocketAddressToInterger(clientSocket)) && false;
    }

}
