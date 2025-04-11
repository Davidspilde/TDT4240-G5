package io.github.Spyfall.services.websocket;

import java.net.URISyntaxException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class LocalWebSocketClient extends WebSocketClient {

    private static LocalWebSocketClient instance;
    private RecieveMessageService reciever;

    private LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));

        reciever = RecieveMessageService.GetInstance();
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
    }

    @Override
    public void onMessage(String message) {
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

}
