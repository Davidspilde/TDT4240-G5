package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.Map;

public class RoundEndedMessage extends GameMessage {
	private final int roundNumber;
	private final String reason;
	private final boolean spyCaught;
	private final boolean spyGuessCorrect;
	private final String spy;
	private final String location;
	private final Map<String, Integer> scoreboard;

	public RoundEndedMessage(
			int roundNumber,
			String reason,
			boolean spyCaught,
			boolean spyGuessCorrect,
			String spy,
			String location,
			Map<String, Integer> scoreboard) {
		super("roundEnded");
		this.roundNumber = roundNumber;
		this.reason = reason;
		this.spyCaught = spyCaught;
		this.spyGuessCorrect = spyGuessCorrect;
		this.spy = spy;
		this.location = location;
		this.scoreboard = scoreboard;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public String getReason() {
		return reason;
	}

	public boolean isSpyCaught() {
		return spyCaught;
	}

	public boolean isSpyGuessCorrect() {
		return spyGuessCorrect;
	}

	public String getSpy() {
		return spy;
	}

	public String getLocation() {
		return location;
	}

	public Map<String, Integer> getScoreboard() {
		return scoreboard;
	}
}
