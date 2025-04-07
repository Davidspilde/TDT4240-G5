package io.github.Spyfall.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.view.GameLobby;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.GameStage;

import java.util.Map;

public class RecieveMessageService {
    private static RecieveMessageService instance;
    private final JsonReader jsonReader;
    private final Json json;
    private StageManager stageManager;

    private RecieveMessageService() {
        jsonReader = new JsonReader();
        json = new Json();
        stageManager = StageManager.getInstance();
    }

    public static RecieveMessageService getInstance() {
        if (instance == null) {
            instance = new RecieveMessageService();
        }
        return instance;
    }

    public void handleMessage(String message) {
        System.out.println("Handling message: " + message);
        
        try {
            JsonValue jsonValue = jsonReader.parse(message);
            String type = jsonValue.getString("event");
            System.out.println("Message type: " + type);
            
            if (type.isEmpty()) {
                System.out.println("Warning: Empty message type, trying to get 'type' field instead");
                type = jsonValue.getString("type", "");
                System.out.println("Message type from 'type' field: " + type);
            }
            
            switch (type) {
                case "lobbyCreated":
                    String lobbyCode = jsonValue.getString("lobbyCode");
                    String host = jsonValue.getString("host");
                    handleLobbyCreated(lobbyCode, host);
                    break;
                    
                case "joinedLobby":
                    System.out.println("Parsing joinedLobby message");
                    LobbyJoinedMessage joined = json.fromJson(LobbyJoinedMessage.class, message);
                    handleLobbyJoined(joined);
                    break;
                    
                case "lobbyNewHost":
                    System.out.println("Parsing lobbyNewHost message");
                    LobbyNewHostMessage newHost = json.fromJson(LobbyNewHostMessage.class, message);
                    handleLobbyNewHost(newHost);
                    break;
                    
                case "lobbyUpdate":
                    System.out.println("Parsing lobbyUpdate message");
                    LobbyPlayersMessage players = json.fromJson(LobbyPlayersMessage.class, message);
                    handleLobbyPlayers(players);
                    break;

                case "newRound":
                    handleNewRound(jsonValue);
                    break;

                case "roundEnded":
                    handleRoundEnded(jsonValue);
                    break;

                case "gameComplete":
                    handleGameComplete(jsonValue);
                    break;

                case "spyCaught":
                    handleSpyCaught(jsonValue);
                    break;

                case "spyNotCaught":
                    handleSpyNotCaught(jsonValue);
                    break;

                case "spyGuessCorrect":
                    handleSpyGuessCorrect(jsonValue);
                    break;

                case "spyGuessIncorrect":
                    handleSpyGuessIncorrect(jsonValue);
                    break;

                case "error":
                    handleError(jsonValue);
                    break;
                    
                default:
                    System.out.println("Unknown message type: " + type);
                    System.out.println("Message content: " + message);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error parsing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLobbyCreated(String lobbyCode, String host) {
        System.out.println("Handling lobby created: " + lobbyCode + ", host: " + host);
        Gdx.app.postRunnable(() -> {
            ScreenViewport viewport = new ScreenViewport();
            GameLobby gameLobby = new GameLobby(lobbyCode, host, host, viewport);
            stageManager.setStage(gameLobby);
        });
    }

    private void handleLobbyJoined(LobbyJoinedMessage message) {
        System.out.println("Handling lobby joined: " + message);
        System.out.println("Creating GameLobby stage");
        Gdx.app.postRunnable(() -> {
            String username = message.getUsername() != null ? message.getUsername() : SendMessageService.getInstance().getUsername();
            System.out.println("Lobby joined - lobbyCode: " + message.getLobbyCode() + ", host: " + message.getHost() + ", username: " + username);
            
            // Ensure the username is set in SendMessageService
            SendMessageService.getInstance().setUsername(username);
            
            StageManager.getInstance().setStage(new GameLobby(message.getLobbyCode(), message.getHost(), username, new ScreenViewport()));
        });
    }

    private void handleLobbyNewHost(LobbyNewHostMessage msg) {
        System.out.println("Handling new lobby host: " + msg);
    }

    private void handleLobbyPlayers(LobbyPlayersMessage msg) {
        System.out.println("Handling lobby players: " + msg);
        Gdx.app.postRunnable(() -> {
            StageView currentStage = stageManager.getStage();
            if (currentStage instanceof GameLobby) {
                GameLobby gameLobby = (GameLobby) currentStage;
                gameLobby.updatePlayerList(msg.getPlayers());
            }
        });
    }

    private void handleResponse(ResponseMessage msg) {
        System.out.println("Handling generic response: " + msg);
    }

    private void handleNewRound(JsonValue jsonValue) {
        System.out.println("Handling new round");
        int roundNumber = jsonValue.getInt("roundNumber");
        int roundDuration = jsonValue.getInt("roundDuration");
        String role = jsonValue.getString("role");
        String location = role.equals("Spy") ? null : jsonValue.getString("location");

        Gdx.app.postRunnable(() -> {
            StageManager.getInstance().setStage(new GameStage(
                jsonValue.getString("lobbyCode"),
                SendMessageService.getInstance().getUsername(),
                role,
                location,
                roundNumber,
                roundDuration,
                new ScreenViewport()
            ));
        });
    }

    private void handleRoundEnded(JsonValue jsonValue) {
        System.out.println("Handling round ended");
        String spy = jsonValue.getString("spy");
        Map<String, Integer> scoreboard = json.fromJson(Map.class, jsonValue.get("scoreboard").toString());
        
        Gdx.app.postRunnable(() -> {
            StageView currentStage = stageManager.getStage();
            if (currentStage instanceof GameStage) {
                GameStage gameStage = (GameStage) currentStage;
                gameStage.updateScoreboard(scoreboard);
            }
        });
    }

    private void handleGameComplete(JsonValue jsonValue) {
        System.out.println("Handling game complete");
        Map<String, Integer> scoreboard = json.fromJson(Map.class, jsonValue.get("scoreboard").toString());
        
        Gdx.app.postRunnable(() -> {
            StageView currentStage = stageManager.getStage();
            if (currentStage instanceof GameStage) {
                GameStage gameStage = (GameStage) currentStage;
                gameStage.updateScoreboard(scoreboard);
                // TODO: Show game complete dialog and return to lobby
            }
        });
    }

    private void handleSpyCaught(JsonValue jsonValue) {
        System.out.println("Handling spy caught");
        String spy = jsonValue.getString("spy");
        int votes = jsonValue.getInt("votes");
        // TODO: Show spy caught dialog
    }

    private void handleSpyNotCaught(JsonValue jsonValue) {
        System.out.println("Handling spy not caught");
        // TODO: Show spy not caught dialog
    }

    private void handleSpyGuessCorrect(JsonValue jsonValue) {
        System.out.println("Handling spy guess correct");
        String spy = jsonValue.getString("spy");
        String location = jsonValue.getString("location");
        // TODO: Show spy guess correct dialog
    }

    private void handleSpyGuessIncorrect(JsonValue jsonValue) {
        System.out.println("Handling spy guess incorrect");
        String spy = jsonValue.getString("spy");
        String location = jsonValue.getString("location");
        // TODO: Show spy guess incorrect dialog
    }

    private void handleError(JsonValue jsonValue) {
        String errorMessage = jsonValue.getString("message");
        System.out.println("Error from server: " + errorMessage);
        // TODO: Show error dialog to user
    }
}
