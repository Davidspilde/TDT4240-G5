package io.github.Spyfall.client;

import java.net.URISyntaxException;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.services.LocalWebSocketClient;
import io.github.Spyfall.controller.GameController;
import io.github.Spyfall.model.GameModel;

public class GameClient {
    private GameController gameController;
    //private GameModel gameModel;
    private LocalWebSocketClient webSocketClient;

    public GameClient(ScreenViewport viewport) {
        // Initialize WebSocket client first
        webSocketClient = LocalWebSocketClient.getInstance("ws://localhost:8080/ws/game");
        webSocketClient.connect();
        
        // Initialize game model
        //this.gameModel = GameModel.getInstance();
        
        // Initialize game controller
        gameController = new GameController(viewport);
    }

    public void resize(int width, int height) {
        gameController.resize(width, height);
    }

    public void update() {
        gameController.update();
    }
    
    public void dispose() {
        // Close the WebSocket connection
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }
}
