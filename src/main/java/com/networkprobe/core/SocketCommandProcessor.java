package com.networkprobe.core;

import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.model.CidrNotation;
import com.networkprobe.core.model.Command;
import com.networkprobe.core.exception.ApplicationOrigin;
import com.networkprobe.core.exception.ClientRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.*;

/**
 * SocketCommandProcessor é responsável por processar a informação recebida no Socket TCP e determinar
 * se o cliente pode requisitar o comando solicitado através do identificador de rede.
 * */
@Singleton(creationType = SingletonType.DYNAMIC)
public class SocketCommandProcessor implements SocketDataMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SocketCommandProcessor.class);

    @ManagedDependency
    private Template template;

    @Override
    public String processSocketMessage(String clientSentValue, final Socket socket) {

        try {

            CommandRequest commandRequest = new CommandRequest(clientSentValue);
            Command requestedCommand = template.fromRequest(commandRequest);

            if (requestedCommand == null)
                return template.unknownResponse();

            ResponseEntity<?> responseEntity = requestedCommand.getResponse();

            if (isClientNetAllowed(socket, requestedCommand.getRoutes())) {
                if (NetworkProbeOptions.isDebugSocketEnabled())
                    LOG.debug("SOCKET: \"{}\" requisitou \"{}\" e foi aceito / respondido.",
                            socket.getInetAddress().getHostAddress(), clientSentValue);

                return Template.parameterized(responseEntity, commandRequest.arguments());
            }
            if (NetworkProbeOptions.isDebugSocketEnabled())
                LOG.debug("\"{}\" requisitou \"{}\" e foi negado pelo bloqueio de rotas.",
                        socket.getInetAddress().getHostAddress(), clientSentValue);

        }

        catch (ClientRequestException requestException) {
            return requestException.toJSONMessage(ApplicationOrigin.NPS);
        }

        return template.unauthorizedResponse();
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
