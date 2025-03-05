package io.github.Spyfall.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.client.observers.StateListener;
import io.github.Spyfall.ecs.ECSManager;
import io.github.Spyfall.states.MainMenuState;
import io.github.Spyfall.states.State;
import io.github.Spyfall.states.StateManager;
import io.github.Spyfall.states.States;

public class GameClient {
    private StateManager stateManager;
    private ECSManager ecsManager;
    private State currentState;
    private OrthographicCamera camera;
    private Viewport viewport;

    public GameClient(OrthographicCamera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;
        ecsManager = ECSManager.getInstance();
        stateManager = StateManager.getInstance(this);
        stateManager.setState(States.MAIN_MENU);
    }

    public void update(){
        ecsManager.update();
    }

    public void onStateChanged(State currentState) {
        this.currentState = currentState;
        ecsManager.clearEntities();
        currentState.initState(ecsManager,camera,viewport);
    }
}
