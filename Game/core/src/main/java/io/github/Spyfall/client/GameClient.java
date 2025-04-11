package io.github.Spyfall.client;

import java.net.URISyntaxException;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.LocalWebSocketClient;
import io.github.Spyfall.services.RecieveMessageService;
import io.github.Spyfall.controller.MainController;

public class GameClient {
    private MainController mainController;
    //private GameModel gameModel;
    private LocalWebSocketClient webSocketClient;

    public GameClient(ScreenViewport viewport) {
        // init WebSocket client first
        webSocketClient = LocalWebSocketClient.getInstance("ws://localhost:8080/ws/game");
        webSocketClient.connect();

        // init game model
        //this.gameModel = GameModel.getInstance();

        // init game controller
        mainController = MainController.getInstance(viewport);

        RecieveMessageService.GetInstance().setupMessageHandling();

        AudioService.getInstance().playMusic("background",true);
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
