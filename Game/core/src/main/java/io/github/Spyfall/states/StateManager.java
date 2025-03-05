package io.github.Spyfall.states;

import io.github.Spyfall.client.GameClient;


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
    public void setState(States newState){
        switch (newState) {
            case CREATE_LOBBY -> this.currentState = new CreateGameState(this);
            case JOIN_LOBBY -> this.currentState = new JoinGameState(this);
            case MAIN_MENU -> this.currentState = new MainMenuState(this);
        }
        listener.onStateChanged(this.currentState);
    }


    public <T> boolean currentState(Class<T> classType){
        return classType.isInstance(currentState);
    }
}
