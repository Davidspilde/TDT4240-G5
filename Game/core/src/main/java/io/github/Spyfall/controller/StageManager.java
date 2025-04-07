package io.github.Spyfall.controller;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.view.StageView;

public class StageManager {
    private StageView currentStage;
    private static StageManager instance;
    private ScreenViewport viewport;

    private StageManager(){
    }

    public static StageManager getInstance(){
        return (instance == null) ? (instance = new StageManager()) : instance;
    }
    public void setStage(StageView newState){
        System.out.println("Setting new stage: " + newState.getClass().getSimpleName());
        if (currentStage != null) {
            System.out.println("Disposing old stage: " + currentStage.getClass().getSimpleName());
            currentStage.dispose();
        }
        currentStage = newState;
        System.out.println("New stage set: " + currentStage.getClass().getSimpleName());
    }

    public StageView getStage(){
        return currentStage;
    }


    public <T> boolean currentStage(Class<T> classType){
        return classType.isInstance(currentStage);
    }
}
