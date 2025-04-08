package io.github.Spyfall.controller;

import io.github.Spyfall.view.StageView;

public class StageManager {
    private StageView currentStage;
    private static StageManager instance;

    private StageManager(){
    }

    public static StageManager getInstance(){
        return (instance == null) ? (instance = new StageManager()) : instance;
    }
    public void setStage(StageView newState){
        currentStage = newState;
    }

    public StageView getStage(){
        return currentStage;
    }


    public <T> boolean currentStage(Class<T> classType){
        return classType.isInstance(currentStage);
    }
}
