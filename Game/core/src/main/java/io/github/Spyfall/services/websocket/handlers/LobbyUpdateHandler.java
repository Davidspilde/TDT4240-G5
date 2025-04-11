package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.LobbyUpdateMessage;

public class LobbyUpdateHandler implements WebSocketMessageHandler<LobbyUpdateMessage> {

    public lobbyUpdateHandler() {
    }

    @Override
    public String getEvent() {
        return "lobbyUpdate";
    }

    @Override
    public Class<LobbyUpdateMessage> getMessageClass() {
        return LobbyUpdateMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {

    }
}
