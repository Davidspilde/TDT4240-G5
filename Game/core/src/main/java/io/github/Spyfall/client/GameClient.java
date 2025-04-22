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
        String uri = Config.getWebSocketUri();
        webSocketClient = LocalWebSocketClient.getInstance(uri);
        webSocketClient.connect();

        // Loads all the assets which will be used
        AssetLoader.load();

        // init main controller
        mainController = MainController.getInstance(viewport);

        // starts music
        AudioService.getInstance().playMusic("background", true);
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
