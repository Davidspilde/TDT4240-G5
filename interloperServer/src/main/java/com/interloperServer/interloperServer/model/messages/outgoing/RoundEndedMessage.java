package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.Map;

/**
 * Represents an outgoing message sent by the server to notify players about the
 * end of a round.
 * <p>
 * This message contains the following fields:
 * <ul>
 * <li><b>roundNumber</b>: The number of the round that has ended.</li>
 * <li><b>reason</b>: The reason why the round ended (e.g., VOTES, SPY_GUESS,
 * TIMEOUT).</li>
 * <li><b>spyCaught</b>: A boolean indicating whether the spy was caught.</li>
 * <li><b>spyGuessCorrect</b>: A boolean indicating whether the spy's guess of
 * the location was correct.</li>
 * <li><b>spy</b>: The username of the spy for the round.</li>
 * <li><b>location</b>: The location for the round.</li>
 * <li><b>scoreboard</b>: A map where the keys are player usernames and the
 * values are their scores.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "roundEnded").</li>
 * </ul>
 */
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
