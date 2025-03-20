package io.github.Spyfall.stages;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.Main;
import io.github.Spyfall.client.GameClient;

public class StageManager {
    private MainMenuStage currentStage;
    private static StageManager instance;
    private final GameClient listener;
    private ScreenViewport viewport;

    private StageManager(GameClient listener){
        this.listener = listener;
        this.viewport = listener.viewport;
    }

    public static StageManager getInstance(GameClient listener){
        return (instance == null) ? (instance = new StageManager(listener)) : instance;
    }
    public void setStage(MainMenuStage newState){
        currentStage = newState;
    }

    public MainMenuStage getStage(){
        return currentStage;
    }


    public <T> boolean currentStage(Class<T> classType){
        return classType.isInstance(currentStage);
    }
}
