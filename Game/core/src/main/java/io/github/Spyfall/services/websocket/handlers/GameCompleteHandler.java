package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.GameCompleteMessage;

public class GameCompleteHandler implements WebSocketMessageHandler<GameCompleteMessage> {

    @Override
    public String getEvent() {
        return "newRound";
    }

    @Override
    public Class<GameCompleteMessage> getMessageClass() {
        return GameCompleteMessage.class;
    }

    @Override
    public void handle(GameCompleteMessage message) {

    }

}
