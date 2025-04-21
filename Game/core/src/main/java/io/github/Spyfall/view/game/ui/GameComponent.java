package io.github.Spyfall.view.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Simple base class for UI components
 */
public abstract class GameComponent {
    protected Skin skin;
    protected Table rootTable;
    
    public GameComponent(Skin skin) {
        this.skin = skin;
        this.rootTable = new Table();
        create();
    }
    
    /**
     * Create the component's UI elements
     */
    protected abstract void create();
    
    /**
     * Update the component state
     */
    public abstract void update();
    
    /**
     * Get the component's root actor
     */
    public Actor getActor() {
        return rootTable;
    }
    
    /**
     * Clean up any resources
     */
    public void dispose() {
    }
}