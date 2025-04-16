package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveMessage;
import com.interloperServer.interloperServer.service.LobbyManagerService;

@Component
public class JoinLobbyHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final LobbyManagerService lobbyManager;

    public JoinLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "joinLobby";
    }

    @Override
    public Class<RecieveMessage> getMessageClass() {
        return RecieveMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {
        lobbyManager.joinLobby(session, message.getLobbyCode(), message.getUsername());
    }
}
