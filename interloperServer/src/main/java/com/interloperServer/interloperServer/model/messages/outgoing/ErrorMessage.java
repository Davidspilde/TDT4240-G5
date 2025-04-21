package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing error message sent by the server.
 * <p>
 * This message contains the following field:
 * <ul>
 * <li><b>message</b>: A string describing the error.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "error").</li>
 * </ul>
 */
public class ErrorMessage extends GameMessage {
	private final String message;

	public ErrorMessage(String message) {
		super("error");
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
