package com.networkprobe;


import com.networkprobe.core.Template;
import com.networkprobe.core.command.caching.ProcessedResponseEntity;
import com.networkprobe.core.config.JsonTemplateLoader;
import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.config.model.Command;
import com.networkprobe.core.factory.CommandResponseFactory;
import com.networkprobe.core.api.ResponseEntityFactory;
import com.networkprobe.core.networking.MappedNetworkFunctions;
import com.networkprobe.core.reflection.ClassMapperHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private static final MappedNetworkFunctions MAPPED_NETWORK_FUNCTIONS = new MappedNetworkFunctions();

    public static void main(String[] args) throws Exception {

        LOG.info("\n\n _______          __                       __     __________             ___.           \n" +
                " \\      \\   _____/  |___  _  _____________|  | __ \\______   \\______  ____\\_ |__   ____  \n" +
                " /   |   \\_/ __ \\   __\\ \\/ \\/ /  _ \\_  __ \\  |/ /  |     ___|_  __ \\/  _ \\| __ \\_/ __ \\ \n" +
                "/    |    \\  ___/|  |  \\     (  <_> )  | \\/    <   |    |    |  | \\(  <_> ) \\_\\ \\  ___/ \n" +
                "\\____|__  /\\___  >__|   \\/\\_/ \\____/|__|  |__|_ \\  |____|    |__|   \\____/|___  /\\___  >\n" +
                "        \\/     \\/                              \\/                             \\/     \\/ \n\n");
        LOG.info("Iniciando Network Probe...");

        final ClassMapperHandler handler = new ClassMapperHandler();
        handler.extract(MAPPED_NETWORK_FUNCTIONS);

        final ResponseEntityFactory responseEntityFactory = new CommandResponseFactory(handler);

        final TemplateLoader jsonTemplateLoader = new JsonTemplateLoader();
        jsonTemplateLoader.load(new File("./template.json"), responseEntityFactory);

        Template template = new Template(jsonTemplateLoader);

        for (int i = 0; i < 10; i++) {
            Command command = template.getCommand("alterdata_shop");
            ProcessedResponseEntity responseEntity = (ProcessedResponseEntity) command.getResponse();
            String response = responseEntity.getContent();
            System.out.println(response);
        }

    }


}
