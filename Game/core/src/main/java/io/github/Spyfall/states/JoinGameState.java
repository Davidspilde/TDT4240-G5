package io.github.Spyfall.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.ecs.ECSManager;

public class JoinGameState extends State {
    protected JoinGameState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void initState(ECSManager ecsManager, OrthographicCamera camera, Viewport viewport) {

    }


}
