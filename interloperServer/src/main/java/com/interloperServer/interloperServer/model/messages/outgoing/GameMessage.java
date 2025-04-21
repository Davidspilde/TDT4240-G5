package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents a generic outgoing message sent by the server.
 * <p>
 * This class contains the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "error",
 * "gameComplete").</li>
 * </ul>
 * 
 * <p>
 * This class serves as a base class for more specific outgoing message types,
 * which extend this class to include additional fields.
 */
public class GameMessage {
	private final String event;

	public GameMessage(String event) {
		this.event = event;
	}

	public String getEvent() {
		return event;
	}
}
