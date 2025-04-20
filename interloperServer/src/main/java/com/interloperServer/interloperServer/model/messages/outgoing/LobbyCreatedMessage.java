package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing message sent by the server when a lobby is
 * successfully created.
 * <p>
 * This message contains the following fields:
 * <ul>
 * <li><b>lobbyCode</b>: The code of the newly created lobby.</li>
 * <li><b>host</b>: The username of the player who created the lobby and is the
 * host.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "lobbyCreated").</li>
 * </ul>
 */
public class LobbyCreatedMessage extends GameMessage {
	private final String lobbyCode;
	private final String host;

	public LobbyCreatedMessage(String lobbyCode, String host) {
		super("lobbyCreated");
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
