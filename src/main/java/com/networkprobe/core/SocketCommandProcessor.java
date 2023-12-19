package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.domain.CidrNotation;
import com.networkprobe.core.domain.Command;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.exception.ClientRequestException;
import com.networkprobe.core.util.Debugging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

/**
 * SocketCommandProcessor é responsável por processar a informação recebida no Socket TCP e determinar
 * se o cliente pode requisitar o comando solicitado através do identificador de rede.
 * */
@Singleton(creationType = SingletonType.DYNAMIC)
@Documented(done = false)
public class SocketCommandProcessor implements SocketDataMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SocketCommandProcessor.class);

    @Handled
    private Template template;

    @Override
    public String processSocketMessage(String receivedMessage, Socket connectedSocket, ClientHandler clientHandler) {
        try {
            CommandRequest commandRequest = new CommandRequest(receivedMessage);
            Command requestedCommand = template.fromRequest(commandRequest);

            if (requestedCommand == null) {
                return template.unknownResponse();
            }

            ResponseEntity<?> responseEntity = requestedCommand.getResponse();
            clientHandler.getClientMetrics().pushCommandToClientCommandHistory(commandRequest.command());

            if (!isClientNetAllowed(connectedSocket, requestedCommand.getRoutes()))
            {
                Debugging.log(LOG, "\"{}\" requisitou \"{}\" e foi negado pelo bloqueio de rotas.",
                        connectedSocket.getInetAddress().getHostAddress(), receivedMessage);
                return template.unauthorizedResponse();
            }

            Debugging.log(LOG, "SOCKET: \"{}\" requisitou \"{}\" e foi aceito / respondido.",
                    connectedSocket.getInetAddress().getHostAddress(), receivedMessage);

            return Template.parameterized(responseEntity, commandRequest.arguments());

        } catch (ClientRequestException requestException) {
            return requestException.toJSONMessage();
        }

    }

    private boolean isClientNetAllowed(Socket clientSocket, Set<CidrNotation> allowedNetworks) {
        byte[] clientAddressArray = clientSocket.getInetAddress().getAddress();
        for (CidrNotation networkCidr : allowedNetworks) {
            if (isInAllowedSubnet(clientAddressArray, networkCidr))
                return true;
        }
        return false;
    }

    private boolean isInAllowedSubnet(byte[] address, CidrNotation allowedNetwork) {
        return Arrays.equals(calculateNetworkId(address, allowedNetwork
                .getSubnetMask()), allowedNetwork.getNetwork());
    }

    private byte[] calculateNetworkId(byte[] socketAddress, byte[] subnetMask) {
        byte[] networkId = new byte[socketAddress.length];
        for (int i = 0; i < networkId.length; i++) {
            networkId[i] = (byte) (socketAddress[i] & subnetMask[i]);
        }
        return networkId;
    }


}
