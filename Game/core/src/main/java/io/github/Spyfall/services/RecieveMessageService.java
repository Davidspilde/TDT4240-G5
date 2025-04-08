package io.github.Spyfall.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import io.github.Spyfall.message.response.*;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;

public class RecieveMessageService {
    private static RecieveMessageService instance;
    private final JsonReader jsonReader;
    private final Json json;
    private final GameModel gameModel;

    private RecieveMessageService() {
        jsonReader = new JsonReader();
        json = new Json();
        gameModel = GameModel.getInstance();
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

        // ADd new case to get new messagetype, connect with a handler to assign logic
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

            default:
                System.out.println("Unknown message type: " + type);
                break;
        }
    }

    // Handlers for each message type
    private void handleGameComplete(GameCompleteMessage msg) {
        System.out.println("Handling game complete: " + msg);

        System.out.println("Game complete received: " + msg.getScoreboard());
        
        // return to lobby?
        //gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleNewRound(GameNewRoundMessage msg) {
        System.out.println("Handling new round: " + msg);

        System.out.println("New round received: Round " + msg.getRoundNumber());
        
        // update model with new data
        
        gameModel.getGameData().setCurrentRound(msg.getRoundNumber());
        gameModel.getGameData().setTimeRemaining(msg.getRoundDuration());
        
        // bruh
        boolean isSpy = (msg.getRole() != null && msg.getRole().equalsIgnoreCase("spy"));
        gameModel.getGameData().setSpy(isSpy);
        
        // set location and role
        gameModel.getGameData().setLocation(msg.getLocation());
        gameModel.getGameData().setRole(msg.getRole());
        
        // potential locations for spy
        if (isSpy) {
            // should fetch from backend
            List<String> defaultLocations = new ArrayList<>(Arrays.asList(
                "Airplane", "Bank", "Beach", "Casino", "Hospital", 
                "Hotel", "Military Base", "Movie Studio", "Ocean Liner", 
                "Passenger Train", "Restaurant", "School", "Space Station", 
                "Submarine", "Supermarket", "University"
            ));
            gameModel.getGameData().setPossibleLocations(defaultLocations);
        }
        
        // change game state if not there already
        if (gameModel.getCurrentState() != GameState.IN_GAME) {
            gameModel.setCurrentState(GameState.IN_GAME);
        }
    }

    private void handleRoundEnded(GameRoundEndedMessage msg) {
        System.out.println("Handling round ended: " + msg);

        // TODO:
        // Update scoreboard if needed
        // Wait for the next round to start
    }

    private void handleSpyCaught(GameSpyCaughtMessage msg) {
        System.out.println("Handling spy caught: " + msg);

        // TODO: Show spy
    }

    private void handleSpyGuess(GameSpyGuessMessage msg) {
        System.out.println("Handling spy guess: " + msg);

        // TODO: Show guess
    }

    private void handleVote(GameVoteMessage msg) {
        System.out.println("Handling vote: " + msg);

        // TODO: Show votes? vote counter?
    }

    private void handleLobbyCreated(LobbyCreatedMessage msg) {
        System.out.println("Handling lobby created: " + msg.getLobbyCode());

        // update the model with lobby info
        gameModel.setLobbyCode(msg.getLobbyCode());
        gameModel.getLobbyData().setHostPlayer(msg.getHost());
        
        // add player to the player list
        gameModel.getLobbyData().getPlayers().clear();
        gameModel.getLobbyData().addPlayer(gameModel.getUsername());
        
        // transition to lobby state
        gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleLobbyJoined(LobbyJoinedMessage msg) {
        System.out.println("Handling lobby joined: " + msg);

        // update model
        gameModel.setLobbyCode(msg.getLobbyCode());
        gameModel.getLobbyData().setHostPlayer(msg.getHost());
        
        // transition to lobby state
        gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleLobbyNewHost(LobbyNewHostMessage msg) {
        System.out.println("Handling new lobby host: " + msg);

        // update model
        gameModel.getLobbyData().setHostPlayer(msg.getHost());
    }

    private void handleLobbyPlayers(LobbyPlayersMessage msg) {
        System.out.println("Handling lobby players: " + msg);

        // update the model with player list
        gameModel.getLobbyData().setPlayers(msg.getPlayers());
    }

    private void handleResponse(ResponseMessage msg) {
        System.out.println("Handling generic response: " + msg);
    }
}
