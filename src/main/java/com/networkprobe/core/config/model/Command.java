package com.networkprobe.core.config.model;

import com.networkprobe.core.api.ResponseEntity;

import java.util.*;

import static com.networkprobe.core.util.Validator.checkIsNotNull;
import static com.networkprobe.core.util.Validator.checkIsNullOrEmpty;

public class Command {

    private String name;
    private ResponseEntity<?> response;
    private Set<String> routes;
    private boolean cachedOnce;

    public Command(String name, ResponseEntity<?> response, Set<String> routes, boolean cachedOnce) {
        this.name = name;
        this.response = response;
        this.routes = routes;
        this.cachedOnce = cachedOnce;
    }

    private Command() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResponseEntity<?> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<?> response) {
        this.response = response;
    }

    public Set<String> getRoutes() {
        return routes;
    }

    private void setRoutes(Set<String> routes) {
        this.routes = routes;
    }

    public boolean isCachedOnce() {
        return cachedOnce;
    }

    private void setCachedOnce(boolean cachedOnce) {
        this.cachedOnce = cachedOnce;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", rawResponse='" + response.getRawContent() + '\'' +
                ", routes=" + routes +
                ", cachedOnce=" + cachedOnce +
                '}';
    }

    public static final class Builder {

        private final Command command = new Command();

        public Builder name(String name) {
            checkIsNullOrEmpty(name, "name");
            command.setName(name);
            return this;
        }

        public Builder through(String... routes) {
            command.setRoutes(new HashSet<>());
            if (routes != null && routes.length != 0) {
                Arrays.stream(routes)
                        .forEach(command.getRoutes()::add);
            }
            return this;
        }

        public Builder response(ResponseEntity<?> responseEntity) {
            checkIsNotNull(responseEntity, "responseEntity");
            command.setResponse(responseEntity);
            return this;
        }

        public Builder cachedOnce(boolean cachedOnce) {
            command.setCachedOnce(cachedOnce);
            return this;
        }

        public Command get() {
            return command;
        }

    }

}
