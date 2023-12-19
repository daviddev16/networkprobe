package com.networkprobe;

import com.networkprobe.core.NetworkProbeOptions;
import com.networkprobe.core.NetworkServicesFacade;
import com.networkprobe.core.SingletonDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {

        Thread.currentThread().setName("network-probe-main");

        if (NetworkProbeOptions.STAGING)
            args = new String[]{"--debug-sockets", "--template", "./template.yml"};

        LOG.info("\n\n _______          __                       __     __________             ___.           \n" +
                " \\      \\   _____/  |___  _  _____________|  | __ \\______   \\______  ____\\_ |__   ____  \n" +
                " /   |   \\_/ __ \\   __\\ \\/ \\/ /  _ \\_  __ \\  |/ /  |     ___|_  __ \\/  _ \\| __ \\_/ __ \\ \n" +
                "/    |    \\  ___/|  |  \\     (  <_> )  | \\/    <   |    |    |  | \\(  <_> ) \\_\\ \\  ___/ \n" +
                "\\____|__  /\\___  >__|   \\/\\_/ \\____/|__|  |__|_ \\  |____|    |__|   \\____/|___  /\\___  >\n" +
                "        \\/     \\/                              \\/                             \\/     \\/ \n\n");

        LOG.info("Iniciando Network Probe...");

        SingletonDirectory.registerCustomInstance(new NetworkProbeOptions(args));
        SingletonDirectory.registerAllDeclaredSingletonClasses();

        NetworkServicesFacade.getNetworkServices().launchAllServices();

    }

}
