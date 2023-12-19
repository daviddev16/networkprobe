package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.statistics.ClientMetrics;
import com.networkprobe.core.util.Debugging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.networkprobe.core.util.Utility.closeQuietly;

@Documented(done = false)
public class ClientHandler extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
    private static final AtomicInteger ID = new AtomicInteger(-1);

    private static final SocketDataMessageProcessor messageProcessor;

    static
    {
        messageProcessor = (SocketDataMessageProcessor)
                SingletonDirectory.getBasedDependency(SocketDataMessageProcessor.class);
    }

    private final ClientMetrics clientMetrics;
    private Socket clientSocket;
    private PrintWriter outputWriter;
    private Scanner inputScanner;

    private ClientHandler(Socket clientSocket, ClientMetrics clientMetrics) {
        super("client-worker" + ID.incrementAndGet(), false, false);
        this.clientMetrics = clientMetrics;
        this.clientSocket = clientSocket;
        try {
            outputWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
            inputScanner = new Scanner(this.clientSocket.getInputStream());
        } catch (Exception exception)
        {
            ExceptionHandler.handleUnexpected(LOG, exception, Reason.TCP_SOCKET_BEGIN);
            shutdownClientHandler();
        }
    }

    @Override
    public void onBegin() {
        try {
            while (inputScanner.hasNextLine())
            {
                String receivedContent = inputScanner.nextLine();
                long start = System.currentTimeMillis();
                String processedResponse = messageProcessor.processSocketMessage(receivedContent, clientSocket, this);
                Debugging.log(LOG, String.format("SOCKET: Message processor levou %dms para processar a " +
                        "requisição.", (System.currentTimeMillis() - start)));
                outputWriter.println(processedResponse);
                outputWriter.write(-1);
                outputWriter.flush();
            }
        } catch (Exception exception) {
            ExceptionHandler.handleUnexpected(LOG, exception, Reason.TCP_CLIENT_HANDLER_PROCESS);
        } finally {
            closeQuietly(clientSocket, outputWriter, inputScanner);
        }
    }

    @Override
    protected void onUpdate() {
        /* o processamento dos comandos deve ser feito no onBegin, caso chegue
        no onUpdate a conexão deve ser forçada a ser encerrada */
        if (clientSocket.isConnected())
            shutdownClientHandler();
    }

    public static void delegateHandlerTo(final Socket clientSocket,
                                         final ClientMetrics clientMetrics)
    {
        new ClientHandler(clientSocket, clientMetrics).start();
    }

    private void shutdownClientHandler() {
        closeQuietly(this.clientSocket, outputWriter, inputScanner);
        stop();
    }

    public ClientMetrics getClientMetrics() {
        return clientMetrics;
    }

}