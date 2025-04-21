package com.interloperServer.interloperServer.model;

import java.util.List;

/**
 * Represents a location in the game.
 * <p>
 * Each location has a name and a list of roles associated with it. Locations
 * are used during gameplay to assign roles to players and provide context for
 * the game.
 */
public class Location {

    private String name;
    private List<String> roles;

    public Location() {
    }

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
