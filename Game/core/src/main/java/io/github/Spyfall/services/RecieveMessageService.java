package io.github.Spyfall.services;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import io.github.Spyfall.controller.MainController;
import io.github.Spyfall.handlers.MessageHandler;
import io.github.Spyfall.message.response.*;


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
            
            // parse message based on type
            ResponseMessage parsedMessage = parseMessage(type, message);
            System.out.println("parsed message:" + parsedMessage);
            
            if (parsedMessage == null) {
                System.err.println("Failed to parse message of type: " + type);
                return;
            }
            
            if (messageHandler == null) {
                // later processing
                // messageQueue.add(parsedMessage);
                System.out.println("EL BRUH MOMENTO " + type);
            } else {
                // process message immediately
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

    public void setupMessageHandling() {
        try {
            MainController mainController = MainController.getInstance();
            System.out.println("Message handling is now set up");
            // Now we can process any queued messages
        } catch (RuntimeException e) {
            // MainController not ready yet
            System.out.println("MainController not ready yet, will try again later");
        }
    }

    
}
