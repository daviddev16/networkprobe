package com.networkprobe.core.config.model;

import static com.networkprobe.core.util.NetworkUtil.*;
import static com.networkprobe.core.util.Validator.*;

public class Route {

    public static final Route DEFAULT_ROUTE = new Route("internal_system_route", Cidr.NONE);

    private String name;
    private Cidr cidr;

    private Route(String name, Cidr cidr) {
        this.name = name;
        this.cidr = cidr;
    }

    private Route() {}

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Cidr getCidr() {
        return cidr;
    }

    private void setCidr(Cidr cidr) {
        this.cidr = cidr;
    }

    @Override
    public String toString() {
        return "Route{" +
                "name='" + name + '\'' +
                ", cidr=" + cidr +
                '}';
    }

    public static final class Builder {

        private Route route = new Route();

        public Builder name(String name) {
            checkIsNullOrEmpty(name, "name");
            route.setName(name);
            return this;
        }

        public Builder cidr(String cidrNotation) {
            route.setCidr(convertStringToCidr(cidrNotation));
            return this;
        }

        public Route get() {
            return route;
        }

    }

}
