package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import org.apache.commons.cli.*;

import java.io.File;

@Documented(done = false)
public class NetworkProbeOptions {

    public static final boolean STAGING = System.getenv("STAGING") != null;
    public static final String VERSION = "2.0-SNAPSHOT";

    private static boolean debugSocket;
    private static boolean debugDirectoryLogs;
    private static File templateFile;

    public NetworkProbeOptions(String[] args) throws ParseException
    {
        SingletonDirectory.denyInstantiation(this);
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = new Options()
        {{
            addOption("ds", "debug-sockets", false, "debug socket purpose");
            addOption("do", "directory-logs", false, "SingletonDirectory logs");
            addRequiredOption("t", "template", true, "template file");
        }};
        CommandLine commandLine = commandLineParser.parse(options, args);
        templateFile = new File(commandLine.getOptionValue("template"));
        debugSocket = commandLine.hasOption("debug-sockets");
        debugDirectoryLogs = commandLine.hasOption("directory-logs");
    }

    public static boolean isDebugSocketEnabled() {
        return debugSocket;
    }

    public static boolean isDirectoryLogsEnabled() {
        return debugDirectoryLogs;
    }

    public static File getTemplateFile() {
        return templateFile;
    }

}
