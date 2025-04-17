package com.interloperServer.interloperServer.model.messages.outgoing;

public class NewRoundMessage extends GameMessage {
	private final int roundNumber;
	private final int roundDuration;
	private final String role;
	private final String firstQuestioner;
	private final String location; // optional (null for spy)

	public NewRoundMessage(int roundNumber, int roundDuration, String role, String firstQuestioner, String location) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.firstQuestioner = firstQuestioner;
		this.location = location;
	}

	public NewRoundMessage(int roundNumber, int roundDuration, String role, String firstQuestioner) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.firstQuestioner = firstQuestioner;
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

	public String getFirstQuestioner() {
		return firstQuestioner;
	}
}
