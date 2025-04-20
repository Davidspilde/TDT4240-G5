package io.github.Spyfall.client;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.MainController;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.websocket.LocalWebSocketClient;

public class GameClient {
    private final MainController mainController;
    // private GameModel gameModel;
    private final LocalWebSocketClient webSocketClient;

    public GameClient(ScreenViewport viewport) {
        // init WebSocket client first
        webSocketClient = LocalWebSocketClient.getInstance("ws://localhost:8080/ws/game");
        webSocketClient.connect();

        // init game model
        // this.gameModel = GameModel.getInstance();

        // init game controller
        AudioService.getInstance().playMusic("background", true);
        mainController = MainController.getInstance(viewport);
    }

    public void resize(int width, int height) {
        mainController.resize(width, height);
    }

    public void update() {
        mainController.update();
    }

    public void dispose() {
        // close con
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }
}
