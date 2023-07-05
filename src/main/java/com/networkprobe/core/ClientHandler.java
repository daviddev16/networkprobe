package com.networkprobe.core;

import com.networkprobe.core.api.SocketDataMessageProcessor;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.command.SocketCommandProcessor;
import com.networkprobe.core.config.CidrNotation;
import com.networkprobe.core.config.Command;
import com.networkprobe.core.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler extends ExecutionWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private static final SocketDataMessageProcessor PROCESSOR = new SocketCommandProcessor();
    private static final Template template = JsonTemplateAdapter.getTemplateInstance();
    private static final AtomicInteger ID = new AtomicInteger(-1);

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        super("client-worker" + ID.incrementAndGet(), false, false);
        this.clientSocket = clientSocket;
    }

    @Override
    public void onBegin() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner in = new Scanner(clientSocket.getInputStream());
            while (in.hasNextLine()) {
                String message = in.nextLine();
                String response = PROCESSOR.processSocketMessage(message.trim(), clientSocket);
                out.println(response);
            }
        } catch (Exception e) {
            ExceptionHandler.unexpected(LOG, e, 190);
        }
    }

    @Override
    public void onStop() {
        NetworkUtil.closeQuietly(clientSocket);
        ID.decrementAndGet();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
