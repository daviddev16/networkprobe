package com.networkprobe.core.entity.internal;

import com.networkprobe.core.NetworkProbeOptions;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.CommandEntity;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.entity.caching.CachedResponseEntity;
import com.networkprobe.core.entity.caching.CachedValue;

import java.net.InetAddress;
import java.net.UnknownHostException;

@CommandEntity(commandName = "version")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
@Documented(done = false)
public class VersionInfoResponseEntity extends CachedResponseEntity {

    public VersionInfoResponseEntity() {
        super("{}", false);
    }

    @Override
    public void cache() {
        setCachedValue(CachedValue.createInstant("Network Probe Services rodando na versão \""
                + NetworkProbeOptions.VERSION + "\" em \"" + getHostName() + "\"."));
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Não identificado";
        }
    }

}
