package io.github.Spyfall.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import io.github.Spyfall.controller.MainController;
import io.github.Spyfall.handlers.MessageHandler;
import io.github.Spyfall.message.response.*;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.view.ui.ErrorPopup;


public class RecieveMessageService {
    private static RecieveMessageService instance;
    private final JsonReader jsonReader;
    private final Json json;
    private MessageHandler messageHandler;

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

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    
    }

    public void handleMessage(String message) {
        try {
            JsonValue root = jsonReader.parse(message);
            String type = root.getString("event", "");

            System.out.println("Received message of type: " + type);

            if (type.equals("error")) {
                handleErrorMessage(root);
                return;
            }

            ResponseMessage parsedMessage = parseMessage(type, message);
            
            
            if (parsedMessage == null) {
                System.err.println("Failed to parse message of type: " + type);
                return;
            } else {
                messageHandler.handleMessage(parsedMessage);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
        
    }

    private ResponseMessage parseMessage(String type, String message) {
        try {
            switch (type) {
                case "gameComplete":
                    return json.fromJson(GameCompleteMessage.class, message);
                    
                case "newRound":
                    return json.fromJson(GameNewRoundMessage.class, message);
                    
                case "roundEnded":
                    return json.fromJson(GameRoundEndedMessage.class, message);
                    
                case "gameSpyCaught":
                    return json.fromJson(GameSpyCaughtMessage.class, message);
                    
                case "gameSpyGuess":
                    return json.fromJson(GameSpyGuessMessage.class, message);
                    
                case "gameVote":
                    return json.fromJson(GameVoteMessage.class, message);
                    
                case "lobbyCreated":
                    return json.fromJson(LobbyCreatedMessage.class, message);
                    
                case "joinedLobby":
                    return json.fromJson(LobbyJoinedMessage.class, message);
                    
                case "lobbyNewHost":
                    return json.fromJson(LobbyNewHostMessage.class, message);
                    
                case "lobbyUpdate":
                    return json.fromJson(LobbyPlayersMessage.class, message);
                    
                default:
                    System.out.println("Unknown message type: " + type);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parsing message of type " + type + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
        private void handleErrorMessage(JsonValue messageData) {
            String errorEvent = messageData.getString("event", "unknown");
            String errorMessage = messageData.getString("message", "Unknown error occurred");
            
            // Show the error popup on the UI thread
            Gdx.app.postRunnable(() -> {
                ErrorPopup.getInstance().showServerError(errorEvent, errorMessage);
            });
        }

    // private void handleLobbyCreated(LobbyCreatedMessage msg) {
    //     String lobbyCode = msg.getLobbyCode();
    //     System.out.println("Handling lobby created: " + lobbyCode);
        
    //     Gdx.app.postRunnable(() -> {
    //         gameModel.setLobbyCode(lobbyCode);
    //         gameModel.getLobbyData().setHostPlayer(msg.getHost());
            
    //         // add player to the player list
    //         gameModel.getLobbyData().getPlayers().clear();
    //         gameModel.getLobbyData().addPlayer(gameModel.getUsername());
            
    //         // transition to game config state
    //         gameModel.setCurrentState(GameState.GAME_CONFIG);
    //     });
    // }

    // private void handleLobbyJoined(LobbyJoinedMessage msg) {
    //     System.out.println("Handling lobby joined: " + msg);
        
    //     Gdx.app.postRunnable(() -> {
    //         // update model
    //         gameModel.setLobbyCode(msg.getLobbyCode());
    //         gameModel.getLobbyData().setHostPlayer(msg.getHost());
            
    //         // transition to game config state instead of lobby state
    //         gameModel.setCurrentState(GameState.GAME_CONFIG);
    //     });
    // }

    public void setupMessageHandling() {
        try {
            MainController mainController = MainController.getInstance();
            System.out.println("Message handling is now set up");
        } catch (RuntimeException e) {
            System.out.println("MainController not ready yet, will try again later");
        }
    }

    
}
