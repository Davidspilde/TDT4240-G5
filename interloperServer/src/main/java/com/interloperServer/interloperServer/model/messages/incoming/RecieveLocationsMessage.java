package com.interloperServer.interloperServer.model.messages.incoming;

import java.util.List;
import com.interloperServer.interloperServer.model.Location;

/**
 * Represents an incoming message for updating the list of locations in the
 * game.
 * <p>
 * The message contains the following field:
 * <ul>
 * <li><b>locations</b>: A list of {@link Location} objects representing the
 * locations in the game.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link RecieveMessage}, which includes the following
 * fields:
 * <ul>
 * <li><b>type</b>: The type of the message (e.g., "locationsUpdate").</li>
 * <li><b>lobbyCode</b>: The code of the lobby associated with the message.</li>
 * <li><b>username</b>: The username of the player sending the message.</li>
 * </ul>
 */
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
