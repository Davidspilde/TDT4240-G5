package io.github.Spyfall.services.websocket.handlers.lobbyHandlers;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyUpdateMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class LobbyUpdateHandler implements WebSocketMessageHandler<LobbyUpdateMessage> {

    @Override
    public String getEvent() {
        return "lobbyUpdate";
    }

    @Override
    public Class<LobbyUpdateMessage> getMessageClass() {
        return LobbyUpdateMessage.class;
    }

    @Override
    public void handle(LobbyUpdateMessage message) {
        LobbyController lobbyController = LobbyController.getInstance();

        lobbyController.handleLobbyUpdate(message.getPlayers());

    }

}
