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

    public GameClient(ScreenViewport viewport) {
        try {
            webSocketClient = new LocalWebSocketClient("ws://localhost:8080/ws/game");
        } catch (URISyntaxException e) {
        }
        webSocketClient.connect();
        stageManager = StageManager.getInstance();
        stageManager.setStage(new MainMenuStage(viewport));

    }

    public void onStateChanged(MainMenuStage currentStage) {
    }

    public void resize(int width, int height) {
        stageManager.getStage().resize(width, height);
    }

    public void update() {
        stageManager.getStage().update();
    }
}
