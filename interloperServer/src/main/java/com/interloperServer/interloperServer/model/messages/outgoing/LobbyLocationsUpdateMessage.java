package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.List;

import com.interloperServer.interloperServer.model.Location;

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
