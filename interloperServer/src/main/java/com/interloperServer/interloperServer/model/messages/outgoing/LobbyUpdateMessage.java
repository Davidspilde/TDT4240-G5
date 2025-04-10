package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.List;

public class LobbyUpdateMessage extends GameMessage {
	private final List<String> players;

	public LobbyUpdateMessage(List<String> players) {
		super("lobbyUpdate");
		this.players = players;
	}

	public List<String> getPlayers() {
		return players;
	}
}
