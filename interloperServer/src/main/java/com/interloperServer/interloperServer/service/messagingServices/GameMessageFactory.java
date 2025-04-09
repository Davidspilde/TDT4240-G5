package com.interloperServer.interloperServer.service.messagingServices;

import java.util.List;

import org.springframework.stereotype.Component;

import com.interloperServer.interloperServer.model.messages.outgoing.*;

@Component
public class GameMessageFactory {

    public ErrorMessage error(String message) {
        return new ErrorMessage(message);
    }

    public GameMessage gameStarted() {
        return new GameMessage("gameStarted");
    }

    public NewRoundMessage newRound(int number, int duration, String role, String location) {
        return new NewRoundMessage(number, duration, role, location);
    }

    public LobbyCreatedMessage lobbyCreated(String lobbyCode, String host) {
        return new LobbyCreatedMessage(lobbyCode, host);
    }

    public JoinedLobbyMessage joinedLobby(String lobbyCode, String host) {
        return new JoinedLobbyMessage(lobbyCode, host);
    }

    public NewHostMessage newHost(String host) {
        return new NewHostMessage(host);
    }

    public LobbyUpdateMessage lobbyUpdate(List<String> players) {
        return new LobbyUpdateMessage(players);
    }

}
