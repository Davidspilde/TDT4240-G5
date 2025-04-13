package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.ResponseMessage;

public class GameEndedHandler implements WebSocketMessageHandler<ResponseMessage> {

    @Override
    public String getEvent() {
        return "gameEnded";
    }

    @Override
    public Class<ResponseMessage> getMessageClass() {
        return ResponseMessage.class;
    }

    @Override
    public void handle(ResponseMessage message) {

    }

}
