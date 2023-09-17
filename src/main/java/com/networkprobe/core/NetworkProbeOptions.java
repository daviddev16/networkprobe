package com.networkprobe.core;

import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

public class NetworkProbeOptions {

    private static boolean debugSocket;
    private static File templateFile;

    public NetworkProbeOptions(String[] args) throws ParseException
    {
        SingletonDirectory.denyInstantiation(this);
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = new Options()
        {{
            addOption("ds", "debug-sockets", false, "debug socket purpose");
            addRequiredOption("t", "template", true, "template file");
        }};
        CommandLine commandLine = commandLineParser.parse(options, args);
        templateFile = new File(commandLine.getOptionValue("template"));
        debugSocket = commandLine.hasOption("debug-sockets");
    }

    public static boolean isDebugSocketEnabled() {
        return debugSocket;
    }

    public static File getTemplateFile() {
        return templateFile;
    }

}
