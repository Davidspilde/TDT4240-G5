package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing message sent by the server to notify players about the
 * spy's last attempt.
 * <p>
 * This message contains the following fields:
 * <ul>
 * <li><b>spyUsername</b>: The username of the spy.</li>
 * <li><b>lastAttemptDuration</b>: The duration of the spy's last attempt in
 * seconds.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "spyLastAttempt").</li>
 * </ul>
 */
public class SpyLastAttemptMessage extends GameMessage {
	private String spyUsername;
	private int lastAttemptDuration;

	public SpyLastAttemptMessage(String spyUsername, int lastAttemptDuration) {
		super("spyLastAttempt");
		this.spyUsername = spyUsername;
		this.lastAttemptDuration = lastAttemptDuration;
	}

	public int getLastAttemptDuration() {
		return lastAttemptDuration;
	}

	public String getSpyUsername() {
		return spyUsername;
	}

}
