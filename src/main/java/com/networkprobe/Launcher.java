package com.networkprobe;

import com.networkprobe.core.config.JsonTemplateLoader;
import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.factory.CommandResponseFactory;
import com.networkprobe.core.api.ResponseEntityFactory;
import com.networkprobe.core.networking.MappedNetworkFunctions;
import com.networkprobe.core.networking.NetworkServicesFacade;
import com.networkprobe.core.reflection.ClassMapperHandler;
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

        NetworkServicesFacade.initialize();

        final ClassMapperHandler handler = new ClassMapperHandler();
        handler.extract(MappedNetworkFunctions.COMMON);

        final ResponseEntityFactory responseEntityFactory = new CommandResponseFactory(handler);

        final TemplateLoader jsonTemplateLoader = new JsonTemplateLoader();
        jsonTemplateLoader.load(new File("./template.json"), responseEntityFactory);

        NetworkServicesFacade.getInstance().launchAllServices(jsonTemplateLoader);

    }

}
