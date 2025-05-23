package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

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
        GameplayController gameplayController = GameplayController.getInstance();
        gameplayController.handleNewRound(message.getRoundNumber(), message.getRoundDuration(), message.getRole(), message.getLocation(), message.getFirstQuestioner());
    }

}
