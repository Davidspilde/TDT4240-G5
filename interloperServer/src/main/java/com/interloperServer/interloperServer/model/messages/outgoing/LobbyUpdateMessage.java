package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.List;

/**
 * Represents an outgoing message sent by the server to update the list of
 * players in the lobby.
 * <p>
 * This message contains the following field:
 * <ul>
 * <li><b>players</b>: A list of player usernames currently in the lobby.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "lobbyUpdate").</li>
 * </ul>
 */
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
