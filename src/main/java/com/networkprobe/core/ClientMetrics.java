package com.networkprobe.core;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientMetrics {

    private Queue<String> requestedCommands = new ArrayDeque<>();
    private int tcpBlockedConnections = 0;
    private int udpBlockedConnections = 0;
    private int tcpConnectionCount = 0;
    private int udpReceivedCount = 0;

    public ClientMetrics() {}

    public synchronized void queueCommandRequest(String command) {
        if (command != null && !command.isEmpty()) {
            if (requestedCommands.size() > 3) {
                requestedCommands.remove();
            }
            requestedCommands.add(command);
        }
    }

    public synchronized void countTcpAcceptedConnection() {
        tcpConnectionCount += 1;
    }

    public synchronized void countTcpBlockedConnection() {
        tcpConnectionCount += 1;
    }

    public synchronized void countUdpAcceptedConnection() {
        udpBlockedConnections += 1;
    }
    public synchronized void countUdpBlockedConnection() {
        udpBlockedConnections += 1;
    }

    public Queue<String> getRequestedCommands() {
        return requestedCommands;
    }

    public int getTcpBlockedConnections() {
        return tcpBlockedConnections;
    }

    public int getUdpBlockedConnections() {
        return udpBlockedConnections;
    }

    public int getTcpConnectionCount() {
        return tcpConnectionCount;
    }

    public int getUdpReceivedCount() {
        return udpReceivedCount;
    }
}
