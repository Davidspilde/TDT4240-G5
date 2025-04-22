package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class GameCompleteHandler implements WebSocketMessageHandler<GameCompleteMessage> {

    @Override
    public String getEvent() {
        return "gameComplete";
    }

    @Override
    public Class<GameCompleteMessage> getMessageClass() {
        return GameCompleteMessage.class;
    }

    @Override
    public void handle(GameCompleteMessage message) {
        GameplayController gameplayController = GameplayController.getInstance();

        gameplayController.handleGameComplete(message.getScoreboard());

    }

}
