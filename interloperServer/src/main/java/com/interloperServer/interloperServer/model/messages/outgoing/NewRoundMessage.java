package com.interloperServer.interloperServer.model.messages.outgoing;

public class NewRoundMessage extends GameMessage {
	private final int roundNumber;
	private final int roundDuration;
	private final String role;
	private final String location; // optional (null for spy)

	public NewRoundMessage(int roundNumber, int roundDuration, String role, String location) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.location = location;
	}

	public NewRoundMessage(int roundNumber, int roundDuration, String role) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.location = null;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public int getRoundDuration() {
		return roundDuration;
	}

	public String getRole() {
		return role;
	}

	public String getLocation() {
		return location;
	}
}
