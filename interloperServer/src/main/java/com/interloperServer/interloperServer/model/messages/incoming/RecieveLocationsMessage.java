package com.interloperServer.interloperServer.model.messages.incoming;

import java.util.List;

import com.interloperServer.interloperServer.model.Location;

public class RecieveLocationsMessage extends RecieveMessage {
    private List<Location> locations;

    public RecieveLocationsMessage() {

    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

}
