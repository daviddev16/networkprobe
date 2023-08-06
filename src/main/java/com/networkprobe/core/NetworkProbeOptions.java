package com.networkprobe.core;

import org.apache.commons.cli.*;

public class NetworkProbeOptions {

    private static boolean DEBUG_SOCKET;
    private final CommandLine commandLine;

    public NetworkProbeOptions(String[] args) throws ParseException
    {
        SingletonDirectory.denyInstantiation(this);
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = new Options()
        {{
            addOption("ds", "debug-sockets", false, "debug socket purpose");
        }};
        commandLine = commandLineParser.parse(options, args);
        DEBUG_SOCKET = commandLine.hasOption("debug-sockets");
    }

    public static boolean isDebugSocketEnabled() {
        return DEBUG_SOCKET;
    }

}
