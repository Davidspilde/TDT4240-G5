package io.github.Spyfall.services.websocket.handlers;

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
        System.out.println("Lobby created success");

    }

}
