package io.github.Spyfall.services.websocket;

import java.net.URISyntaxException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.badlogic.gdx.Gdx;

public class LocalWebSocketClient extends WebSocketClient {

    private static LocalWebSocketClient instance;
    private MessageDispatcher dispatcher;

    private LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));

        dispatcher = MessageDispatcher.GetInstance();
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
        // Sets this on the same thread as the rest of the game logic, avoiding
        // threading issues
        Gdx.app.postRunnable(() -> {
            dispatcher.dispatch(message);
        });
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
