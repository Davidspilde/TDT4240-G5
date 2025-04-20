package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyCreatedMessage;

public class LobbyCreatedHandler implements WebSocketMessageHandler<LobbyCreatedMessage> {

    @Override
    public String getEvent() {
        return "lobbyCreated";
    }

    @Override
    public Class<LobbyCreatedMessage> getMessageClass() {
        return LobbyCreatedMessage.class;
    }

    @Override
    public void handle(LobbyCreatedMessage message) {

        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.lobbyCreated(message.getHost(), message.getLobbyCode());
    }

}
