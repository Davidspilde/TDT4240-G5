package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.Map;

/**
 * Represents an outgoing message sent by the server when the game is complete.
 * <p>
 * This message contains the following field:
 * <ul>
 * <li><b>scoreboard</b>: A map where the keys are player usernames and the
 * values are their scores.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "gameComplete").</li>
 * </ul>
 */
public class GameCompleteMessage extends GameMessage {
	private final Map<String, Integer> scoreboard;

	public GameCompleteMessage(Map<String, Integer> scoreboard) {
		super("gameComplete");
		this.scoreboard = scoreboard;
	}

	public Map<String, Integer> getScoreboard() {
		return scoreboard;
	}
}
