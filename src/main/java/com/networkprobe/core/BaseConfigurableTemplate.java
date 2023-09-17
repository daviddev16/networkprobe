package com.networkprobe.core;

import com.networkprobe.core.model.Command;
import com.networkprobe.core.model.Networking;
import com.networkprobe.core.model.Route;
import com.networkprobe.core.exception.InvalidPropertyException;

import java.util.HashMap;
import java.util.Map;

import static com.networkprobe.core.util.Validator.nonNull;

public class BaseConfigurableTemplate implements Template {

    private Networking networking;
    private Map<String, Route> routes;
    private Map<String, Command> commands;

    public BaseConfigurableTemplate() {
        routes = new HashMap<>();
        commands = new HashMap<>();
    }

    public void configureNetworking(Networking networking) {
        this.networking = nonNull(networking, "networking");
    }

    protected void setRoutes(Map<String, Route> routes) {
        this.routes = nonNull(routes, "routes");
    }

    protected void setCommands(Map<String, Command> commands) {
        this.commands = nonNull(commands, "commands");
    }

    @Override
    public Networking getNetworking() {
        return networking;
    }

    @Override
    public Map<String, Route> getRoutes() {
        return routes;
    }

    @Override
    public Map<String, Command> getCommands() {
        return commands;
    }

    public static InvalidPropertyException incoherentPropertyTypeException(String message) {
        return new InvalidPropertyException( message );
    }
}
