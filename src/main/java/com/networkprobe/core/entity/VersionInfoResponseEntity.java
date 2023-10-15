package com.networkprobe.core.entity;

import com.networkprobe.core.Defaults;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.CommandEntity;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.caching.CachedResponseEntity;
import com.networkprobe.core.caching.CachedValue;

import java.net.InetAddress;
import java.net.UnknownHostException;

@CommandEntity(commandName = "version")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
public class VersionInfoResponseEntity extends CachedResponseEntity {

    public VersionInfoResponseEntity() {
        super("{}", false);
    }

    @Override
    public void cache() {
        setCachedValue(CachedValue.createInstant("Network Probe Services rodando na versão \""
                + Defaults.NPS_VERSION + "\" em \"" + getHostName() + "\"."));
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Não identificado";
        }
    }

}
