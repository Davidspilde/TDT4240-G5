package io.github.Spyfall.services;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import io.github.Spyfall.message.request.*;

public class SendMessageService {
    private LocalWebSocketClient wsClient;
    private static SendMessageService instance;
    private String username;

    private SendMessageService() {
        // the server url must be set before sendMessageService is
        this.wsClient = LocalWebSocketClient.getInstance();
    }

    public static SendMessageService getInstance() {
        if (instance == null) {
            instance = new SendMessageService();
        }
        return instance;
    }

    public boolean createLobby(String username) {
        CreateLobbyMessage msg = new CreateLobbyMessage(username);
        return sendMessage(msg);
    }

    public boolean joinLobby(String username, String lobbyCode) {
        String type = "joinLobby";
        this.username = username;
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);
        System.out.println("Sending join lobby message: " + msg);
        String jsonMessage = convertMessageToJson(msg);
        System.out.println("JSON message: " + jsonMessage);
        return sendMessage(msg);
    }

    public boolean vote(String username, String target, String lobbyCode) {
        String type = "vote";
        VoteMessage msg = new VoteMessage(type, username, target, lobbyCode);
        return sendMessage(msg);
    }

    public boolean spyVote(String username, String Location, String lobbyCode) {
        String type = "spyVote";
        VoteMessage msg = new VoteMessage(type, username, Location, lobbyCode);

        return sendMessage(msg);
    }

    public boolean startNextRound(String username, String lobbyCode) {
        String type = "advanceRound";
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);

        return sendMessage(msg);
    }

    public boolean updateLobbyOptions(String username, String lobbyCode, int roundlimit, int locationNumber,
            int spyCount, int maxPlayers, int timeperRound) {
        LobbyOptionsMessage msg = new LobbyOptionsMessage(username, lobbyCode, roundlimit, locationNumber, spyCount,
                maxPlayers, timeperRound);
        return sendMessage(msg);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println("Username set in SendMessageService: " + username);
    }

    public void startGame(String lobbyCode) {
        System.out.println("Sending start game request for lobby: " + lobbyCode + ", username: " + username);
        RequestMessage message = new RequestMessage("startGame", username, lobbyCode);
        sendMessage(message);
    }

    private boolean sendMessage(Object message) {
        try {
            String jsonMessage = convertMessageToJson(message);
            System.out.println("Sending message: " + jsonMessage);
            wsClient.send(jsonMessage);
            System.out.println("Message sent successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error sending Message: " + e);
            e.printStackTrace();
            return false;
        }
    }

    private String convertMessageToJson(Object message) {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        return json.toJson(message);
    }
}
