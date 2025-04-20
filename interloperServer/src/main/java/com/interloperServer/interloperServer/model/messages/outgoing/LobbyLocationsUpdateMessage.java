package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.List;
import com.interloperServer.interloperServer.model.Location;

/**
 * Represents an outgoing message sent by the server to update the list of
 * locations in the lobby.
 * <p>
 * This message contains the following field:
 * <ul>
 * <li><b>locations</b>: A list of {@link Location} objects representing the
 * updated locations in the lobby.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "locationsUpdate").</li>
 * </ul>
 */
public class LobbyLocationsUpdateMessage extends GameMessage {
	private final List<Location> locations;

	public LobbyLocationsUpdateMessage(List<Location> locations) {
		super("locationsUpdate");
		this.locations = locations;
	}

	public List<Location> getLocations() {
		return locations;
	}
}
