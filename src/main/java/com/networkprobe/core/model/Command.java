package com.networkprobe.core.model;

import com.networkprobe.core.entity.base.ResponseEntity;

import java.util.*;

import static com.networkprobe.core.util.Validator.checkIsNotNull;
import static com.networkprobe.core.util.Validator.checkIsNullOrEmpty;

public class Command {

    private String name;
    private ResponseEntity<?> response;
    private Set<CidrNotation> routes;
    /*TODO:*/
    private List<String> arguments;
    private Set<String> tags;
    private boolean cachedOnce;

    public Command(String name, ResponseEntity<?> response, Set<CidrNotation> routes,
                   boolean cachedOnce, Set<String> tags) {
        this.name = name;
        this.response = response;
        this.routes = routes;
        this.cachedOnce = cachedOnce;
        this.tags = tags;
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

    public Set<CidrNotation> getRoutes() {
        return routes;
    }

    private void setRoutes(Set<CidrNotation> routes) {
        this.routes = routes;
    }

    public boolean isCachedOnce() {
        return cachedOnce;
    }

    private void setCachedOnce(boolean cachedOnce) {
        this.cachedOnce = cachedOnce;
    }

    public Set<String> getTags() {
        return tags;
    }

    private void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static final class Builder {

        private final Command command = new Command();

        {
            command.setRoutes(new HashSet<>());
            command.setTags(new HashSet<>());
        }

        public Builder name(String name) {
            checkIsNullOrEmpty(name, "name");
            command.setName(name);
            return this;
        }

        public Builder network(CidrNotation cidrNotation) {
            checkIsNotNull(cidrNotation, "cidrNotation");
            command.getRoutes().add(cidrNotation);
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

        public Builder addTag(String tag) {
            checkIsNullOrEmpty(tag, "tag");
            command.getTags().add(tag);
            return this;
        }

        public Command get() {
            return command;
        }

    }

}
