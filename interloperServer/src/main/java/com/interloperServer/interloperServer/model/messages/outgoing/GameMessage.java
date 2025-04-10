package com.interloperServer.interloperServer.model.messages.outgoing;

public class GameMessage {
	private final String event;

	public GameMessage(String event) {
		this.event = event;
	}

	public String getEvent() {
		return event;
	}
}
