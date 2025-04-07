package com.interloperServer.interloperServer.model;

import java.util.List;

public class Location {
    private String name;
    private List<String> roles;

    public Location(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;

    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
