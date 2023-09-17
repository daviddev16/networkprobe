package com.networkprobe.core.entity;

import com.networkprobe.core.Defaults;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class InfoResponseEntity implements ResponseEntity<String> {

    private static String HOSTNAME;
    static {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            HOSTNAME = "Não identificado";
        }
    }

    @Override
    public String getContent(List<String> arguments) {
        return "Network Probe Services rodando na versão \""
                + Defaults.NPS_VERSION + "\" em \"" + HOSTNAME + "\".";
    }

    @Override
    public String getRawContent() {
        return getClass().getName();
    }

    @Override
    public boolean isCachedOnce() {
        return true;
    }

}
