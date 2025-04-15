package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.service.LobbyManagerService;
import com.interloperServer.interloperServer.model.messages.incomming.RecieveMessage;

@Component
public class LeaveLobbyHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final LobbyManagerService lobbyManager;

    public LeaveLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "leaveLobby";
    }

    @Override
    public Class<RecieveMessage> getMessageClass() {
        return RecieveMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {
        lobbyManager.leaveLobby(session, message.getLobbyCode(), message.getUsername());
    }
}
