package com.networkprobe;


import com.networkprobe.core.config.JsonTemplateLoader;
import com.networkprobe.core.config.TemplateLoader;
import com.networkprobe.core.factory.CommandResponseFactory;
import com.networkprobe.core.factory.ResponseEntityFactory;
import com.networkprobe.core.networking.NetworkAddressableMethods;
import com.networkprobe.core.reflection.ClassMapperHandler;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class Launcher {

    public static void main(String[] args) throws Exception {


        ClassMapperHandler handler = new ClassMapperHandler();
        handler.extract(NetworkAddressableMethods.class);

        ResponseEntityFactory responseEntityFactory = new CommandResponseFactory(handler);

        TemplateLoader templateLoader = new JsonTemplateLoader();

        templateLoader.load(new File("./template.json"), responseEntityFactory);


        printEntrySet(templateLoader.getCommands().entrySet(), "commands");

    }

    public static void printEntrySet(Set<?> entries, String name) {
        System.out.println(name);
        for(Object entry : entries) {
            if (entry instanceof Map.Entry) {
                System.out.println(((Map.Entry<?, ?>) entry).getKey() + "=" + ((Map.Entry<?, ?>) entry).getValue());
            }
        }
        System.out.println("\n");
    }

}
