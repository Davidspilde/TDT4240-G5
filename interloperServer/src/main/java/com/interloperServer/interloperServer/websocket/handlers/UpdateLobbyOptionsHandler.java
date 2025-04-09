package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.recievedMessages.RecieveLobbyOptionsMessage;
import com.interloperServer.interloperServer.service.LobbyService;

@Component
public class UpdateLobbyOptionsHandler implements WebSocketMessageHandler<RecieveLobbyOptionsMessage> {

    private final LobbyService lobbyService;

    public UpdateLobbyOptionsHandler(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @Override
    public String getType() {
        return "updateOptions";
    }

    @Override
    public Class<RecieveLobbyOptionsMessage> getMessageClass() {
        return RecieveLobbyOptionsMessage.class;
    }

    @Override
    public void handle(RecieveLobbyOptionsMessage message, WebSocketSession session) {
        lobbyService.updateLobbyOptions(message.getLobbyCode(), message);
    }
}
