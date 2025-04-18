package com.interloperServer.interloperServer.model.messages.outgoing;

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
