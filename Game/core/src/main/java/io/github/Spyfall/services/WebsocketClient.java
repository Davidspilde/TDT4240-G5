package io.github.Spyfall.services;

import java.net.URISyntaxException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

class LocalWebSocketClient extends WebSocketClient {

    public LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server");
        send("Hello from LibGDX!");
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
