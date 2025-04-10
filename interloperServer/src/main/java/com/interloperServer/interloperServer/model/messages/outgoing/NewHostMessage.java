package com.interloperServer.interloperServer.model.messages.outgoing;

public class NewHostMessage extends GameMessage {
	private final String host;

	public NewHostMessage(String host) {
		super("newHost");
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
