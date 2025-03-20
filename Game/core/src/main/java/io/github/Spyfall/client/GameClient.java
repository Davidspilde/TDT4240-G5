package io.github.Spyfall.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.stages.MainMenuStage;
import io.github.Spyfall.stages.StageController;
import io.github.Spyfall.stages.StageManager;
import io.github.Spyfall.stages.Stages;

public class GameClient {

    private StageController currentStage;

    public GameClient(ScreenViewport viewport) {
        this.currentStage = new MainMenuStage(viewport);
    }

    public void onStateChanged(MainMenuStage currentStage) {
    }

    public void resize(int width, int height){
        currentStage.resize(width,height);
    }

    public void update(){
        currentStage.update();
    }
}
