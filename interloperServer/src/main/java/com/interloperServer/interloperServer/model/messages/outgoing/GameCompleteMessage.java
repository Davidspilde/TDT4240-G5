package com.interloperServer.interloperServer.model.messages.outgoing;

import java.util.Map;

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
