package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyUpdateMessage;

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

        lobbyController.lobbyUpdate(message.getPlayers());

    }

}
