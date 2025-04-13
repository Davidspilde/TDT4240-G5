package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incomming.RecieveCreateLobbyMessage;
import com.interloperServer.interloperServer.service.LobbyManagerService;

@Component
public class CreateLobbyHandler implements WebSocketMessageHandler<RecieveCreateLobbyMessage> {

    private final LobbyManagerService lobbyManager;

    public CreateLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "createLobby";
    }

    @Override
    public Class<RecieveCreateLobbyMessage> getMessageClass() {
        return RecieveCreateLobbyMessage.class;
    }

    @Override
    public void handle(RecieveCreateLobbyMessage message, WebSocketSession session) {
        lobbyManager.createLobby(session, message.getUsername());
    }
}
