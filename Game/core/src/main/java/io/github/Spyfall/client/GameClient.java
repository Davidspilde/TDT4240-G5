package io.github.Spyfall.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.client.observers.StateListener;
import io.github.Spyfall.ecs.ECSManager;
import io.github.Spyfall.stages.MainMenuStage;
import io.github.Spyfall.stages.StageController;
import io.github.Spyfall.stages.StageManager;
import io.github.Spyfall.stages.Stages;
import io.github.Spyfall.states.MainMenuState;
import io.github.Spyfall.states.State;
import io.github.Spyfall.states.StateManager;
import io.github.Spyfall.states.States;

public class GameClient {
    private StageManager stageManager;
    private ECSManager ecsManager;
    private MainMenuStage currentStage;
    private OrthographicCamera camera;
    public ScreenViewport viewport;

    public GameClient(ScreenViewport viewport) {
        this.viewport = viewport;
        stageManager = StageManager.getInstance(this);
        stageManager.setStage(new MainMenuStage(viewport));
        currentStage = stageManager.getStage();
    }

    public void onStateChanged(MainMenuStage currentStage) {
        this.currentStage = currentStage;
    }

    public void resize(int width, int height){
        currentStage.getStage().getViewport().update(width,height);
    }

    public void update(){
        currentStage.getStage().act();
        currentStage.getStage().draw();
    }
}
