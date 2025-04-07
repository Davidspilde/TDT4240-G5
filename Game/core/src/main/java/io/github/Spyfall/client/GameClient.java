package io.github.Spyfall.client;

import java.net.URISyntaxException;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.services.LocalWebSocketClient;
import io.github.Spyfall.view.MainMenuStage;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.controller.StageManager;

public class GameClient {

    private StageView currentStage;
    private StageManager stageManager;
    private LocalWebSocketClient webSocketClient;
    private boolean hasShownConnectionError = false;
    private long lastReconnectAttempt = 0;
    private static final long RECONNECT_DELAY = 5000; // 5 seconds between reconnect attempts

    public GameClient(ScreenViewport viewport) {
        // Initialize WebSocket client
        webSocketClient = LocalWebSocketClient.getInstance("ws://localhost:8080/ws/game");
        
        // Try to connect to server
        try {
            System.out.println("Connecting to WebSocket server...");
            webSocketClient.connectBlocking();
            System.out.println("WebSocket connection established");
            hasShownConnectionError = false;
        } catch (InterruptedException e) {
            handleConnectionError("Error connecting to WebSocket server. Please ensure the server is running.");
        }

        // Initialize stage manager and set initial stage
        stageManager = StageManager.getInstance();
        stageManager.setStage(new MainMenuStage(viewport));
    }

    public void onStateChanged(MainMenuStage currentStage) {
    }

    public void resize(int width, int height) {
        stageManager.getStage().resize(width, height);
    }

    public void update() {
        // Check WebSocket connection
        if (!webSocketClient.isOpen()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastReconnectAttempt > RECONNECT_DELAY) {
                System.out.println("Attempting to reconnect to WebSocket server...");
                try {
                    webSocketClient.reconnectBlocking();
                    if (webSocketClient.isOpen()) {
                        System.out.println("Successfully reconnected to WebSocket server");
                        hasShownConnectionError = false;
                    }
                } catch (InterruptedException e) {
                    handleConnectionError("Cannot connect to game server. Please ensure the server is running.");
                }
                lastReconnectAttempt = currentTime;
            }
        }
        
        stageManager.getStage().update();
    }

    private void handleConnectionError(String message) {
        if (!hasShownConnectionError) {
            System.err.println(message);
            // TODO: Show error dialog to user
            hasShownConnectionError = true;
        }
    }

    public boolean isConnected() {
        return webSocketClient != null && webSocketClient.isOpen();
    }
}
