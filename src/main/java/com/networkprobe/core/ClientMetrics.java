package com.networkprobe.core;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientMetrics {

    private Queue<String> requestedCommands = new ArrayDeque<>();
    private int tcpConnectionCount = 0;
    private int udpConnectionCount = 0;

    public ClientMetrics() {}

    public synchronized void queueCommandRequest(String command) {
        if (command != null && !command.isEmpty()) {
            if (requestedCommands.size() > 3) {
                requestedCommands.remove();
            }
            requestedCommands.add(command);
        }
    }

    public void countTcpAcceptedConnection() {
        tcpConnectionCount += 1;
    }

    public void countUdpAcceptedConnection() {
        udpConnectionCount += 1;
    }

    public Queue<String> getRequestedCommands() {
        return requestedCommands;
    }

    public int getTcpConnectionCount() {
        return tcpConnectionCount;
    }

    public int getUdpConnectionCount() {
        return udpConnectionCount;
    }
}
