package io.github.Spyfall.services.websocket;

import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import io.github.Spyfall.message.request.*;
import io.github.Spyfall.model.Location;

public class SendMessageService {
    private LocalWebSocketClient wsClient;
    private static SendMessageService instance;

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
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);
        return sendMessage(msg);
    }

    public boolean leaveLobby(String username, String lobbyCode) {
        String type = "leaveLobby";
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);
        return sendMessage(msg);
    }

    public boolean startGame(String username, String lobbyCode) {
        String type = "startGame";
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);
        return sendMessage(msg);
    }

    public boolean vote(String username, String target, String lobbyCode) {
        String type = "vote";
        VoteMessage msg = new VoteMessage(type, username, target, lobbyCode);
        return sendMessage(msg);
    }

    public boolean spyGuess(String username, String Location, String lobbyCode) {
        String type = "spyGuess";
        VoteMessage msg = new VoteMessage(type, username, Location, lobbyCode);

        return sendMessage(msg);
    }

    public boolean endGame(String username, String lobbyCode) {
        String type = "endGame";
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);
        return sendMessage(msg);

    }

    public boolean startNextRound(String username, String lobbyCode) {
        String type = "advanceRound";
        RequestMessage msg = new RequestMessage(type, username, lobbyCode);

        return sendMessage(msg);
    }

    public boolean updateLobbyOptions(String username, String lobbyCode, int roundlimit, int locationNumber,
            int maxPlayers, int timeperRound, int spyLastAttemptTime) {
        LobbyOptionsMessage msg = new LobbyOptionsMessage(username, lobbyCode, roundlimit, locationNumber, maxPlayers,
                timeperRound, spyLastAttemptTime);
        return sendMessage(msg);
    }

    public boolean updateLobbyLocations(String username, String lobbyCode, List<Location> locations) {
        LobbyLocationsMessage msg = new LobbyLocationsMessage(username, lobbyCode, locations);
        return sendMessage(msg);
    }

    private boolean sendMessage(Object message) {
        try {
            wsClient.send(convertMessageToJson(message));
        } catch (Exception e) {
            System.out.println("Error sending Message: " + e);
            return false;
        }

        return true;
    }

    private String convertMessageToJson(Object message) {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        return json.toJson(message);
    }
}
