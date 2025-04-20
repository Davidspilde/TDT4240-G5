package io.github.Spyfall.services.websocket.handlers.GameHandlers;

import java.util.HashMap;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

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
        GameplayController gameplayController = GameplayController.getInstance();
        String spy = message.getSpy();
        String location = message.getLocation();
        String reason = message.getReason();
        int roundNumber = message.getRoundNumber();
        HashMap<String, Integer> scoreboard = message.getScoreboard();

        gameplayController.handleRoundEnded(spy, location, scoreboard, roundNumber, reason);

    }

}
