package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incomming.RecieveCreateLobbyMessage;
import com.interloperServer.interloperServer.service.LobbyService;

@Component
public class CreateLobbyHandler implements WebSocketMessageHandler<RecieveCreateLobbyMessage> {

    private final LobbyService lobbyService;

    public CreateLobbyHandler(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
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
        lobbyService.createLobby(session, message.getUsername());
    }
}
