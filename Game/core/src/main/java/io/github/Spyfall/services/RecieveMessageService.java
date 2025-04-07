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
            JsonValue root = jsonReader.parse(message);
            String type = root.getString("event", "");
            System.out.println("Message type: " + type);
            
            if (type.isEmpty()) {
                System.out.println("Warning: Empty message type, trying to get 'type' field instead");
                type = root.getString("type", "");
                System.out.println("Message type from 'type' field: " + type);
            }
            
            switch (type) {
                case "lobbyCreated":
                    System.out.println("Parsing lobbyCreated message");
                    LobbyCreatedMessage created = json.fromJson(LobbyCreatedMessage.class, message);
                    handleLobbyCreated(created.getLobbyCode(), created.getHost());
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
}
