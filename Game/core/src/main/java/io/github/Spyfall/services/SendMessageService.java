package io.github.Spyfall.services;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import io.github.Spyfall.models.messages.CreateLobbyMessage;
import io.github.Spyfall.models.messages.LobbyOptionsMessage;
import io.github.Spyfall.models.messages.Message;
import io.github.Spyfall.models.messages.VoteMessage;

public class SendMessageService {
    private LocalWebSocketClient wsClient;

    public SendMessageService(LocalWebSocketClient wsClient) {
        this.wsClient = wsClient;

    }

    public boolean createLobby(String username) {
        CreateLobbyMessage msg = new CreateLobbyMessage(username);
        return sendMessage(msg);
    }

    public boolean joinLobby(String username, String lobbyCode) {
        String type = "joinLobby";
        Message msg = new Message(type, username, lobbyCode);
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
        Message msg = new Message(type, username, lobbyCode);

        return sendMessage(msg);
    }

    public boolean updateLobbyOptions(String username, String lobbyCode, int roundlimit, int locationNumber,
            int maxPlayers, int timeperRound) {
        LobbyOptionsMessage msg = new LobbyOptionsMessage(username, lobbyCode, roundlimit, locationNumber, maxPlayers,
                timeperRound);
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
