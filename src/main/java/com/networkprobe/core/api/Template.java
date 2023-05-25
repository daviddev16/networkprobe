package com.networkprobe.core.api;

import com.networkprobe.core.config.model.Command;
import com.networkprobe.core.config.model.Networking;
import com.networkprobe.core.config.model.Route;

import java.util.Map;

public interface Template {

    Networking getNetworking();
    Map<String, Route> getRoutes();
    Map<String, Command> getCommands();

}
