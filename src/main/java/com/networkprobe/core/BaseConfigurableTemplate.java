package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.domain.Command;
import com.networkprobe.core.domain.Networking;
import com.networkprobe.core.domain.Route;
import com.networkprobe.core.exception.InvalidPropertyException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.networkprobe.core.util.Validator.nonNull;

@Documented(done = false)
public abstract class BaseConfigurableTemplate implements Template {

    private Networking networking;
    private Map<String, Route> routes;
    private Map<String, Command> commands;

    public BaseConfigurableTemplate() {
        routes = new HashMap<>();
        commands = new HashMap<>();
    }

    @Override
    public void configureNetworking(Networking networking) {
        this.networking = nonNull(networking, "networking");
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

    public abstract void load(File templateFile, ResponseEntityFactory responseEntityFactory);
}
