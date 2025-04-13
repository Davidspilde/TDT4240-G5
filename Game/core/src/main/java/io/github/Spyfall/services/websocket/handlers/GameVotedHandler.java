package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.ResponseMessage;

public class GameVotedHandler implements WebSocketMessageHandler<ResponseMessage> {

    @Override
    public String getEvent() {
        return "voted";
    }

    @Override
    public Class<ResponseMessage> getMessageClass() {
        return ResponseMessage.class;
    }

    @Override
    public void handle(ResponseMessage message) {

    }

}
