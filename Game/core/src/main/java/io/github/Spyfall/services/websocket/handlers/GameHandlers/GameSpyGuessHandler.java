package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class GameSpyGuessHandler implements WebSocketMessageHandler<GameSpyGuessMessage> {

    @Override
    public String getEvent() {
        return "spyGuess";
    }

    @Override
    public Class<GameSpyGuessMessage> getMessageClass() {
        return GameSpyGuessMessage.class;
    }

    @Override
    public void handle(GameSpyGuessMessage message) {
        GameplayController gameplayController = GameplayController.getInstance();

        // gameplayController.handleSpyGuess(message.getSpy(), message.getLocation());

    }

}
