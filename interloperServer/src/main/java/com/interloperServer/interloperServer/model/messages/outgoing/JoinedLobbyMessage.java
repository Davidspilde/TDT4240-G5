package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing message sent by the server when a player successfully
 * joins a lobby.
 * <p>
 * This message contains the following fields:
 * <ul>
 * <li><b>lobbyCode</b>: The code of the lobby the player has joined.</li>
 * <li><b>host</b>: The username of the host of the lobby.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "joinedLobby").</li>
 * </ul>
 */
public class JoinedLobbyMessage extends GameMessage {
	private final String lobbyCode;
	private final String host;

	public JoinedLobbyMessage(String lobbyCode, String host) {
		super("joinedLobby");
		this.lobbyCode = lobbyCode;
		this.host = host;
	}

	public String getLobbyCode() {
		return lobbyCode;
	}

	public String getHost() {
		return host;
	}
}
