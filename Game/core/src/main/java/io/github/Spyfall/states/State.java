package io.github.Spyfall.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.client.AssetLoader;
import io.github.Spyfall.ecs.ECSManager;

public abstract class State {
    protected StateManager stateManager;
    protected SpriteBatch spriteBatch;
    protected BitmapFont font;


    protected State(StateManager stateManager) {
        this.stateManager = stateManager;
        spriteBatch = new SpriteBatch();
        font = AssetLoader.font;
    }
    public abstract void initState(ECSManager ecsManager, OrthographicCamera camera, Viewport viewport);
}
