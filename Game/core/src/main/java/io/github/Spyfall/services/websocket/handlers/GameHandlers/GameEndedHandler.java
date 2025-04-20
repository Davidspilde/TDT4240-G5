package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

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
