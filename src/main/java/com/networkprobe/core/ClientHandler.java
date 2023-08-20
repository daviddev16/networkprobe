package com.networkprobe.core;

import com.networkprobe.core.util.IOUtil;
import com.networkprobe.core.util.NetworkUtil;
import com.networkprobe.core.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
    private static final AtomicInteger ID = new AtomicInteger(-1);

    private final Socket clientSocket;
    private final PrintWriter outputWriter;
    private final Scanner inputScanner;

    public ClientHandler(Socket clientSocket) throws IOException {
        super("client-worker" + ID.incrementAndGet(), false, false);
        this.outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        this.inputScanner = new Scanner(clientSocket.getInputStream());
        this.clientSocket = clientSocket;
    }

    @Override
    public void onBegin() {
        try {
            SocketDataMessageProcessor messageProcessor = SingletonDirectory.getSingleOf(SocketCommandProcessor.class);
            while (inputScanner.hasNextLine()) {
                String receivedContent = inputScanner.nextLine();
                String response = messageProcessor.processSocketMessage(receivedContent, clientSocket);
                outputWriter.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.unexpected(LOG, e, 190);
        }
    }

    @Override
    public void onStop()
    {
        Utility.closeQuietly(inputScanner, outputWriter, clientSocket);
    }

}
