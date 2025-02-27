package io.github.Spyfall.View;

import com.badlogic.gdx.graphics.OrthographicCamera;

import io.github.Spyfall.Controller.GameStateManager;

public abstract class State {
    protected OrthographicCamera camera;
    protected GameStateManager gameStateManager;

    protected State(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        camera = new OrthographicCamera();
    }
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}
