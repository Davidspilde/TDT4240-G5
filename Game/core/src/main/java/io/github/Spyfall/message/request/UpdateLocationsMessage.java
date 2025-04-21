package io.github.Spyfall.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.Spyfall.model.Location;
import java.util.List;

public class UpdateLocationsMessage extends RequestMessage {
    @JsonProperty("event")
    private String event = "locationsUpdate";
    
    private List<Location> locations;

    public UpdateLocationsMessage() {
        super();
    }

    public UpdateLocationsMessage(String username, String lobbyCode, List<Location> locations) {
        super("UPDATE_LOCATIONS", username, lobbyCode);
        this.locations = locations;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "UpdateLocationsMessage{type='" + type + "', username='" + username + 
               "', lobbyCode='" + lobbyCode + "', locations=" + locations + "}";
    }
} 