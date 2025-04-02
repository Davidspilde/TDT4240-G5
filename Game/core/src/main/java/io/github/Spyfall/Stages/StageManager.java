package io.github.Spyfall.stages;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.client.GameClient;

public class StageManager {
    private MainMenuStage currentStage;
    private static StageManager instance;
    private ScreenViewport viewport;

    private StageManager(){
    }

    public static StageManager getInstance(GameClient listener){
        return (instance == null) ? (instance = new StageManager()) : instance;
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
