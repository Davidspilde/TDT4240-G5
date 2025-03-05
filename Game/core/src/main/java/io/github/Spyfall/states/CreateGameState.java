package io.github.Spyfall.states;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.ecs.ECSManager;

public class CreateGameState extends State {

    public CreateGameState(StateManager stateManager){
        super(stateManager);

    }

    @Override
    public void initState(ECSManager ecsManager, OrthographicCamera camera, Viewport viewport) {

    }

}
