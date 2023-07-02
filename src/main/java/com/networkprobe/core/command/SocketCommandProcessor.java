package com.networkprobe.core.command;

import com.networkprobe.core.JsonTemplateAdapter;
import com.networkprobe.core.api.ResponseEntity;
import com.networkprobe.core.api.SocketDataMessageProcessor;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.config.CidrNotation;
import com.networkprobe.core.config.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Set;

/**
 * SocketCommandProcessor é responsável por processar a informação recebida no Socket TCP e determinar
 * se o cliente pode requisitar o comando solicitado através do identificador de rede.
 * */
public class SocketCommandProcessor implements SocketDataMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SocketCommandProcessor.class);

    private final Template template = JsonTemplateAdapter.getTemplateInstance();

    @Override
    public String processSocketMessage(String message, final Socket socket) {

        Command command = template.getCommands().get(message);
        ResponseEntity<?> responseEntity = command.getResponse();

        if (checkIsClientAllowed(socket.getInetAddress().getAddress(), command.getRoutes()))
            return responseEntity.getContent().toString();

        return "{\"message\": \"unauthorized access\"}";
    }

    private boolean checkIsClientAllowed(byte[] clientAddressArray, Set<CidrNotation> allowedNetworks) {
        for (CidrNotation networkCidr : allowedNetworks) {
            if (accept(clientAddressArray, networkCidr))
                return true;
        }
        return false;
    }

    private boolean accept(byte[] address, CidrNotation allowedNetwork) {
        byte[] networkByteArray = allowedNetwork.getNetwork();
        for (int i = 0; i < address.length; i++) {
            if (networkByteArray[i] != getNetworkId(address, allowedNetwork.getSubnetMask())[i])
                return false;
        }
        return true;
    }

    private byte[] getNetworkId(byte[] socketAddress, byte[] subnetMask) {
        byte[] networkId = new byte[socketAddress.length];
        for (int i = 0; i < networkId.length; i++) {
            networkId[i] = (byte) (socketAddress[i] & subnetMask[i]);
        }
        return networkId;
    }

}
