package io.github.Spyfall.message.response;

import java.util.List;

import io.github.Spyfall.model.Location;

public class LobbyLocationsUpdateMessage extends ResponseMessage {
    private List<Location> locations;

    public LobbyLocationsUpdateMessage() {
    }

    public List<Location> getLocations() {
        return locations;
    }

}
