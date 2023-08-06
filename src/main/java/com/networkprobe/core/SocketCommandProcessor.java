package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.api.ResponseEntity;
import com.networkprobe.core.api.SocketDataMessageProcessor;
import com.networkprobe.core.api.Template;
import com.networkprobe.core.config.CidrNotation;
import com.networkprobe.core.config.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.*;

/**
 * SocketCommandProcessor é responsável por processar a informação recebida no Socket TCP e determinar
 * se o cliente pode requisitar o comando solicitado através do identificador de rede.
 * */
@Singleton(creationType = SingletonType.LAZY)
public class SocketCommandProcessor implements SocketDataMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SocketCommandProcessor.class);
    @Override
    public String processSocketMessage(String message, final Socket socket) {

        Template template = JsonTemplateAdapter.getTemplateInstance();
        Command messagedCommand = commandByMessage(template, message);
        ResponseEntity<?> responseEntity = messagedCommand.getResponse();

        if (messagedCommand == null)
            return template.unknownResponse();

        if (checkIsClientAllowed(socket.getInetAddress().getAddress(), messagedCommand.getRoutes())) {
            if (NetworkProbeOptions.isDebugSocketEnabled()) {
                LOG.debug("SOCKET: \"{}\" requisitou \"{}\" e foi aceito / respondido.",
                        socket.getInetAddress().getHostAddress(), message);
            }
            return responseEntity.getContent().toString();
        }
        if (NetworkProbeOptions.isDebugSocketEnabled()) {
            LOG.debug("\"{}\" requisitou \"{}\" e foi negado pelo bloqueio de rotas.",
                    socket.getInetAddress().getHostAddress(), message);
        }
        return template.unauthorizedResponse();
    }

    private boolean checkIsClientAllowed(byte[] clientAddressArray, Set<CidrNotation> allowedNetworks) {
        for (CidrNotation networkCidr : allowedNetworks) {
            if (checkIsInAllowedSubnet(clientAddressArray, networkCidr))
                return true;
        }
        return false;
    }

    private boolean checkIsInAllowedSubnet(byte[] address, CidrNotation allowedNetwork) {
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

    private Command commandByMessage(Template template, String message) {
        return template.getCommands().get(message);
    }
}
