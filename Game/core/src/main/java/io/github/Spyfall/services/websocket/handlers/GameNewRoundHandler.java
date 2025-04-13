package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.GameNewRoundMessage;

public class GameNewRoundHandler implements WebSocketMessageHandler<GameNewRoundMessage> {

    @Override
    public String getEvent() {
        return "newRound";
    }

    @Override
    public Class<GameNewRoundMessage> getMessageClass() {
        return GameNewRoundMessage.class;
    }

    @Override
    public void handle(GameNewRoundMessage message) {

    }

}
