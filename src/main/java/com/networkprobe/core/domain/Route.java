package com.networkprobe.core.domain;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.util.Utility;

import static com.networkprobe.core.util.Validator.checkIsNullOrEmpty;

@Documented(done = false)
public class Route {

    public static final Route ANY = new Route("any", CidrNotation.ALL);
    public static final Route NONE = new Route("none", CidrNotation.NONE);

    private String name;
    private CidrNotation cidr;

    private Route(String name, CidrNotation cidr) {
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

    public CidrNotation getCidr() {
        return cidr;
    }

    private void setCidr(CidrNotation cidr) {
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
            checkIsNullOrEmpty(cidrNotation, "cidrNotation");
            route.setCidr(Utility.convertStringToCidrNotation(cidrNotation));
            return this;
        }

        public Route get() {
            return route;
        }

    }

}
