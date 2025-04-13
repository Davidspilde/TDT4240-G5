package com.interloperServer.interloperServer.model.messages.outgoing;

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
