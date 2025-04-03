package io.github.Spyfall.services;

import java.net.URISyntaxException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import io.github.Spyfall.model.ChatMessage;

public class LocalWebSocketClient extends WebSocketClient {

    public LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server");
        ChatMessage msg = new ChatMessage("createLobby:joe", "joe");
        Json json = new Json();
        json.setOutputType(OutputType.json);
        send(json.toJson(msg));
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
