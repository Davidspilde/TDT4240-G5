package io.github.Spyfall.message.request;

import java.util.List;

import io.github.Spyfall.model.Location;

public class LobbyLocationsMessage extends RequestMessage {
    private List<Location> locations;

    public LobbyLocationsMessage(String username, String lobbyCode, List<Location> locations) {
        super("updateLocations", username, lobbyCode);
        this.locations = locations;
    }

}
