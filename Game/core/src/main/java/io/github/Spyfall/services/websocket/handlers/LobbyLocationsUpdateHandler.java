package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyLocationsUpdateMessage;

public class LobbyLocationsUpdateHandler implements WebSocketMessageHandler<LobbyLocationsUpdateMessage> {

    @Override
    public String getEvent() {
        return "locationsUpdate";
    }

    @Override
    public Class<LobbyLocationsUpdateMessage> getMessageClass() {
        return LobbyLocationsUpdateMessage.class;
    }

    @Override
    public void handle(LobbyLocationsUpdateMessage message) {
        LobbyController lobbyController = LobbyController.getInstance();

    }

}
