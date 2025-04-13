package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.GameRoundEndedMessage;

public class GameRoundEndedHandler implements WebSocketMessageHandler<GameRoundEndedMessage> {

    @Override
    public String getEvent() {
        return "roundEnded";
    }

    @Override
    public Class<GameRoundEndedMessage> getMessageClass() {
        return GameRoundEndedMessage.class;
    }

    @Override
    public void handle(GameRoundEndedMessage message) {

    }

}
