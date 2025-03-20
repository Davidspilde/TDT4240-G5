package io.github.Spyfall.states;

import io.github.Spyfall.Main;
import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.stages.MainMenuStage;


public class StateManager{

    private State currentState;
    private static StateManager instance;
    private final GameClient listener;

    private StateManager(GameClient listener){
        this.listener = listener;
    }

    public static StateManager getInstance(GameClient listener){
        return (instance == null) ? (instance = new StateManager(listener)) : instance;
    }
    public void setStage(MainMenuStage newState){


    }


    public <T> boolean currentState(Class<T> classType){
        return classType.isInstance(currentState);
    }
}
