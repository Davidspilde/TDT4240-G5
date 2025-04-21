package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing message sent by the server to notify that a new host
 * has been assigned to the lobby.
 * <p>
 * This message contains the following field:
 * <ul>
 * <li><b>host</b>: The username of the new host of the lobby.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "newHost").</li>
 * </ul>
 */
public class NewHostMessage extends GameMessage {
	private final String host;

	public NewHostMessage(String host) {
		super("newHost");
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
