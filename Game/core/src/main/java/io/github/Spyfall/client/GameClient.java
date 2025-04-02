package io.github.Spyfall.client;

import java.net.URISyntaxException;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.services.LocalWebSocketClient;
import io.github.Spyfall.stages.MainMenuStage;
import io.github.Spyfall.stages.StageController;
import io.github.Spyfall.stages.StageManager;
import io.github.Spyfall.stages.Stages;
import io.github.Spyfall.stages.TestStage;

public class GameClient {

    private StageController currentStage;
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
