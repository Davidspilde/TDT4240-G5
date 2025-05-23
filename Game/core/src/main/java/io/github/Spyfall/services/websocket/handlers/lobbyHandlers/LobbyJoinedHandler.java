package io.github.Spyfall.services.websocket.handlers.lobbyHandlers;

import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;
import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyJoinedMessage;

public class LobbyJoinedHandler implements WebSocketMessageHandler<LobbyJoinedMessage> {

    @Override
    public String getEvent() {
        return "joinedLobby";
    }

    @Override
    public Class<LobbyJoinedMessage> getMessageClass() {
        return LobbyJoinedMessage.class;
    }

    @Override
    public void handle(LobbyJoinedMessage message) {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.handleLobbyJoined(message.getHost(), message.getLobbyCode());

    }

}
