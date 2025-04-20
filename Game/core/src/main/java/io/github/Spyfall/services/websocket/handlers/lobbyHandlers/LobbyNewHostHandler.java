package io.github.Spyfall.services.websocket.handlers.lobbyHandlers;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class LobbyNewHostHandler implements WebSocketMessageHandler<LobbyNewHostMessage> {

    @Override
    public String getEvent() {
        return "newHost";
    }

    @Override
    public Class<LobbyNewHostMessage> getMessageClass() {
        return LobbyNewHostMessage.class;
    }

    @Override
    public void handle(LobbyNewHostMessage message) {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.handleLobbyNewHost(message.getHost());

    }

}
