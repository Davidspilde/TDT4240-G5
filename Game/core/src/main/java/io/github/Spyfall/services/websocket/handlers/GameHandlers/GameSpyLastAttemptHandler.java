package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.GameSpyLastAttemptMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class GameSpyLastAttemptHandler implements WebSocketMessageHandler<GameSpyLastAttemptMessage> {

    @Override
    public String getEvent() {
        return "spyLastAttempt";
    }

    @Override
    public Class<GameSpyLastAttemptMessage> getMessageClass() {
        return GameSpyLastAttemptMessage.class;
    }

    @Override
    public void handle(GameSpyLastAttemptMessage message) {
        GameplayController gameplayController = GameplayController.getInstance();

        gameplayController.handleSpyLastAttempt(message.getSpyUsername(), message.getLastAttemptDuration());

    }

}
