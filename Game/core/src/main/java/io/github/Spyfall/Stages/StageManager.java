package io.github.Spyfall.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.client.GameClient;

public class StageManager {
    private StageController currentStage;
    private static StageManager instance;
    private ScreenViewport viewport;

    private StageManager(){
    }

    public static StageManager getInstance(){
        return (instance == null) ? (instance = new StageManager()) : instance;
    }
    public void setStage(StageController newState){
        currentStage = newState;
    }

    public StageController getStage(){
        return currentStage;
    }


    public <T> boolean currentStage(Class<T> classType){
        return classType.isInstance(currentStage);
    }
}
