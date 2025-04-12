package com.interloperServer.interloperServer.model.messages.outgoing;

public class JoinedLobbyMessage extends GameMessage {

	private final String lobbyCode;
	private final String host;

	public JoinedLobbyMessage(String lobbyCode, String host) {
		super("joinedLobby");
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
