package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

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
        GameplayController gameplayController = GameplayController.getInstance();

        gameplayController.handleVote();

    }

}
