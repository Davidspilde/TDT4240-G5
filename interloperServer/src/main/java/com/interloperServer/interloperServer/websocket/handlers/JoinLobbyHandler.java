package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incomming.RecieveMessage;
import com.interloperServer.interloperServer.service.LobbyService;

@Component
public class JoinLobbyHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final LobbyService lobbyService;

    public JoinLobbyHandler(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
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
        lobbyService.joinLobby(session, message.getLobbyCode(), message.getUsername());
    }
}
