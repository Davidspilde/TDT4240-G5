package io.github.Spyfall.services;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

// Import all your message classes
import io.github.Spyfall.message.response.*;

public class RecieveMessageService {
    private static RecieveMessageService instance;
    private final JsonReader jsonReader;
    private final Json json;

    private RecieveMessageService() {
        jsonReader = new JsonReader();
        json = new Json();
    }

    public static RecieveMessageService GetInstance() {
        if (instance == null) {
            instance = new RecieveMessageService();
        }
        return instance;
    }

    public void handleMessage(String message) {
        JsonValue root = jsonReader.parse(message);
        String type = root.getString("event", "");

        switch (type) {
            case "gameComplete":
                GameCompleteMessage gameComplete = json.fromJson(GameCompleteMessage.class, message);
                handleGameComplete(gameComplete);
                break;

            case "gameNewRound":
                GameNewRoundMessage newRound = json.fromJson(GameNewRoundMessage.class, message);
                handleNewRound(newRound);
                break;

            case "gameRoundEnded":
                GameRoundEndedMessage roundEnded = json.fromJson(GameRoundEndedMessage.class, message);
                handleRoundEnded(roundEnded);
                break;

            case "gameSpyCaught":
                GameSpyCaughtMessage spyCaught = json.fromJson(GameSpyCaughtMessage.class, message);
                handleSpyCaught(spyCaught);
                break;

            case "gameSpyGuess":
                GameSpyGuessMessage spyGuess = json.fromJson(GameSpyGuessMessage.class, message);
                handleSpyGuess(spyGuess);
                break;

            case "gameVote":
                GameVoteMessage vote = json.fromJson(GameVoteMessage.class, message);
                handleVote(vote);
                break;

            case "lobbyCreated":
                LobbyCreatedMessage created = json.fromJson(LobbyCreatedMessage.class, message);
                handleLobbyCreated(created);
                break;

            case "lobbyJoined":
                LobbyJoinedMessage joined = json.fromJson(LobbyJoinedMessage.class, message);
                handleLobbyJoined(joined);
                break;

            case "lobbyNewHost":
                LobbyNewHostMessage newHost = json.fromJson(LobbyNewHostMessage.class, message);
                handleLobbyNewHost(newHost);
                break;

            case "lobbyPlayers":
                LobbyPlayersMessage players = json.fromJson(LobbyPlayersMessage.class, message);
                handleLobbyPlayers(players);
                break;

            case "response":
                ResponseMessage response = json.fromJson(ResponseMessage.class, message);
                handleResponse(response);
                break;

            default:
                System.out.println("Unknown message type: " + type);
                break;
        }
    }

    // Handlers for each message type
    private void handleGameComplete(GameCompleteMessage msg) {
        System.out.println("Handling game complete: " + msg);
    }

    private void handleNewRound(GameNewRoundMessage msg) {
        System.out.println("Handling new round: " + msg);
    }

    private void handleRoundEnded(GameRoundEndedMessage msg) {
        System.out.println("Handling round ended: " + msg);
    }

    private void handleSpyCaught(GameSpyCaughtMessage msg) {
        System.out.println("Handling spy caught: " + msg);
    }

    private void handleSpyGuess(GameSpyGuessMessage msg) {
        System.out.println("Handling spy guess: " + msg);
    }

    private void handleVote(GameVoteMessage msg) {
        System.out.println("Handling vote: " + msg);
    }

    private void handleLobbyCreated(LobbyCreatedMessage msg) {
        System.out.println("Handling lobby created: " + msg.getLobbyCode());
    }

    private void handleLobbyJoined(LobbyJoinedMessage msg) {
        System.out.println("Handling lobby joined: " + msg);
    }

    private void handleLobbyNewHost(LobbyNewHostMessage msg) {
        System.out.println("Handling new lobby host: " + msg);
    }

    private void handleLobbyPlayers(LobbyPlayersMessage msg) {
        System.out.println("Handling lobby players: " + msg);
    }

    private void handleResponse(ResponseMessage msg) {
        System.out.println("Handling generic response: " + msg);
    }
}
