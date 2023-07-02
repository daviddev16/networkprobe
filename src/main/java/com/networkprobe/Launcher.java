package com.networkprobe;

import com.networkprobe.core.*;
import com.networkprobe.core.api.TemplateAdapter;
import com.networkprobe.core.CommandResponseFactory;
import com.networkprobe.core.UsableNetworkDataInventory;
import com.networkprobe.core.NetworkServicesFacade;
import com.networkprobe.core.ClassMapperHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {

        Thread.currentThread().setName("network-probe-main");

        LOG.info("\n\n _______          __                       __     __________             ___.           \n" +
                " \\      \\   _____/  |___  _  _____________|  | __ \\______   \\______  ____\\_ |__   ____  \n" +
                " /   |   \\_/ __ \\   __\\ \\/ \\/ /  _ \\_  __ \\  |/ /  |     ___|_  __ \\/  _ \\| __ \\_/ __ \\ \n" +
                "/    |    \\  ___/|  |  \\     (  <_> )  | \\/    <   |    |    |  | \\(  <_> ) \\_\\ \\  ___/ \n" +
                "\\____|__  /\\___  >__|   \\/\\_/ \\____/|__|  |__|_ \\  |____|    |__|   \\____/|___  /\\___  >\n" +
                "        \\/     \\/                              \\/                             \\/     \\/ \n\n");

        LOG.info("Iniciando Network Probe...");

        SingletonDirectory.registerAllDeclaredSingletonClasses();

        ClassMapperHandler.getInstance().extract(UsableNetworkDataInventory.getInventory());

        TemplateAdapter templateAdapter = JsonTemplateAdapter.getTemplateInstance();
        templateAdapter.load(new File("./template.json"), CommandResponseFactory.getFactory());

        NetworkServicesFacade.getNetworkServices().launchAllServices();

    }
}
