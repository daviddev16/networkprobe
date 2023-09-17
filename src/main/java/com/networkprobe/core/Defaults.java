package com.networkprobe.core;

import com.networkprobe.core.model.CidrNotation;
import com.networkprobe.core.model.Command;
import com.networkprobe.core.model.Key;
import com.networkprobe.core.entity.InfoResponseEntity;
import com.networkprobe.core.entity.MetricsResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class Defaults {

    public static final List<String> COMMANDS = new ArrayList<String>()
    {
        { add(Key.CMD_UNAUTHORIZED); add(Key.CMD_UNKNOWN); }
    };

    public static final String NPS_VERSION = "1.0.5-SNAPSHOT";

    public static void createDefaultApplicationCmds(Template template) {

        String metricCmdName = "metrics";
        template.getCommands().put(metricCmdName,
                new Command.Builder()
                        .response(SingletonDirectory
                                .getSingleOf(MetricsResponseEntity.class))
                        .network(CidrNotation.ALL)
                        .name(metricCmdName)
                        .cachedOnce(true)
                        .get());

        String infoCmdName = "info";
        template.getCommands().put(infoCmdName,
                new Command.Builder()
                        .response(new InfoResponseEntity())
                        .network(CidrNotation.ALL)
                        .name(infoCmdName)
                        .cachedOnce(true)
                        .get());
    }

}
