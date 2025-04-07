package io.github.Spyfall.services;

import java.net.URISyntaxException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class LocalWebSocketClient extends WebSocketClient {

    private static LocalWebSocketClient instance;
    private RecieveMessageService reciever;

    private LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));

        reciever = RecieveMessageService.getInstance();
    }

    public static LocalWebSocketClient getInstance(String serverUrl) {
        if (instance == null) {
            try {
                instance = new LocalWebSocketClient(serverUrl);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid WebSocket URI: " + serverUrl, e);
            }
        }
        return instance;
    }

    public static LocalWebSocketClient getInstance() {
        if (instance == null) {
            throw new RuntimeException("Need to set serverURI first");
        }
        return instance;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server");
        System.out.println("WebSocket connection established with server: " + this.getURI());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        reciever.handleMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void send(String message) {
        System.out.println("Sending WebSocket message: " + message);
        try {
            super.send(message);
            System.out.println("WebSocket message sent successfully");
        } catch (Exception e) {
            System.out.println("Error sending WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
