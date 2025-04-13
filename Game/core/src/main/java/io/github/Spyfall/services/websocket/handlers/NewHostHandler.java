package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.LobbyUpdateMessage;

public class NewHostHandler implements WebSocketMessageHandler<newHost> {

    @Override
    public String getEvent() {
        return "lobbyUpdate";
    }

    @Override
    public Class<> getMessageClass() {
        return LobbyUpdateMessage.class;
    }

    @Override
    public void handle(LobbyUpdateMessage message) {

    }

}
