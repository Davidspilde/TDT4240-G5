package com.interloperServer.interloperServer.model.messages.outgoing;

public class LobbyCreatedMessage extends GameMessage {
	private final String lobbyCode;
	private final String host;

	public LobbyCreatedMessage(String lobbyCode, String host) {
		super("lobbyCreated");
		this.lobbyCode = lobbyCode;
		this.host = host;
	}

	public String getLobbyCode() {
		return lobbyCode;
	}

	public String getHost() {
		return host;
	}
}
