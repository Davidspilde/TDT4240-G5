
package io.github.Spyfall.services.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.badlogic.gdx.Gdx;

public class LocalWebSocketClient extends WebSocketClient {

    private static LocalWebSocketClient instance;
    private final MessageDispatcher dispatcher;

    private final long reconnectDelayMillis = 3000; // 3 seconds
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean isReconnecting = false;

    private LocalWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
        this.dispatcher = MessageDispatcher.GetInstance();
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
        isReconnecting = false;
    }

    @Override
    public void onMessage(String message) {
        Gdx.app.postRunnable(() -> dispatcher.dispatch(message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        if (!isOpen()) {
            scheduleReconnect();
        }
    }

    // Schedules a new reeconnect attempt
    private void scheduleReconnect() {
        if (isOpen() || isReconnecting) {
            System.out.println("Skipping reconnect — already connected or reconnecting.");
            return;
        }

        isReconnecting = true;
        System.out.println("Scheduling reconnect in " + reconnectDelayMillis + "ms...");
        reconnectExecutor.schedule(() -> {
            try {
                System.out.println("Trying to reconnect...");
                reconnectBlocking();

                if (isOpen()) {
                    System.out.println("Reconnected successfully!");
                    isReconnecting = false;
                } else {
                    System.out.println("Reconnect failed — not connected.");
                    isReconnecting = false;
                    scheduleReconnect(); // retry
                }
            } catch (Exception e) {
                System.out.println("Reconnect failed with exception: " + e.getMessage());
                isReconnecting = false;
                scheduleReconnect(); // retry
            }
        }, reconnectDelayMillis, TimeUnit.MILLISECONDS);
    }

}
