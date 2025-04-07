package com.interloperServer.interloperServer.model;

import java.util.List;

public class Location {
    private String locationName;
    private List<String> roles;

    public Location(String locationName, List<String> roles) {
        this.locationName = locationName;
        this.roles = roles;

    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
