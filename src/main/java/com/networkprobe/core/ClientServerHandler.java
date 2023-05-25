package com.networkprobe.core;

import com.networkprobe.core.threading.ExecutionWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientServerHandler extends ExecutionWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServerHandler.class);

    private static final AtomicInteger ID = new AtomicInteger(-1);

    private final Socket clientSocket;

    public ClientServerHandler(Socket clientSocket) {
        super("client-worker" + ID.incrementAndGet(), true, false);
        this.clientSocket = clientSocket;
    }

    @Override
    public void onBegin() {
        LOGGER.info("\"{}\" Começou uma comunicação TCP com o servidor.",
                clientSocket.getInetAddress().getHostAddress());
    }

    /*TODO:*/
    @Override
    public void onUpdate() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner in = new Scanner(clientSocket.getInputStream());
            while (in.hasNextLine()) {
                String input = in.nextLine();
                if (input.trim().equalsIgnoreCase("close")) {
                    break;
                }
                out.println("Echo-reply: " + input.trim());
            }
            stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        try {
            if (clientSocket != null)
                clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("Comunicação encerrada.");
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
