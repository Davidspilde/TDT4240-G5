package com.interloperServer.interloperServer.service.messagingServices;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.interloperServer.interloperServer.model.Location;
import com.interloperServer.interloperServer.model.messages.outgoing.*;

@Component
public class GameMessageFactory {

    public ErrorMessage error(String message) {
        return new ErrorMessage(message);
    }

    public GameMessage gameStarted() {
        return new GameMessage("gameStarted");
    }

    public GameMessage gameEnded() {
        return new GameMessage("gameEnded");
    }

    public NewRoundMessage newRound(int number, int duration, String role, String firstQuestioner, String location) {
        return new NewRoundMessage(number, duration, role, firstQuestioner, location);
    }

    public NewRoundMessage newRound(int number, int duration, String role, String firstQuestioner) {

        return new NewRoundMessage(number, duration, role, firstQuestioner);
    }

    public LobbyCreatedMessage lobbyCreated(String lobbyCode, String host) {
        return new LobbyCreatedMessage(lobbyCode, host);
    }

    public JoinedLobbyMessage joinedLobby(String lobbyCode, String host) {
        return new JoinedLobbyMessage(lobbyCode, host);
    }

    public NewHostMessage newHost(String host) {
        return new NewHostMessage(host);
    }

    public LobbyUpdateMessage lobbyUpdate(List<String> players) {
        return new LobbyUpdateMessage(players);
    }

    public GameCompleteMessage gameComplete(Map<String, Integer> scoreboard) {
        return new GameCompleteMessage(scoreboard);
    }

    public SpyLastAttemptMessage spyLastAttempt(String spyUsername, int duration) {
        return new SpyLastAttemptMessage(spyUsername, duration);

    }

    public GameMessage voted() {
        return new GameMessage("voted");
    }

    public LobbyLocationsUpdateMessage locationsUpdate(List<Location> locations) {
        return new LobbyLocationsUpdateMessage(locations);
    }

    public RoundEndedMessage roundEnded(
            int roundNumber,
            String reason,
            boolean spyCaught,
            boolean spyGuessCorrect,
            String spy,
            String location,
            Map<String, Integer> scoreboard) {

        return new RoundEndedMessage(roundNumber, reason, spyCaught, spyGuessCorrect, spy, location, scoreboard);
    }

}
