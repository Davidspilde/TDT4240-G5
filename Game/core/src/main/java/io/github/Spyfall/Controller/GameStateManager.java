package io.github.Spyfall.Controller;

import io.github.Spyfall.View.State;

public class GameStateManager {
    State currentGameState;

    public void setGameState(State gameState) {
        if (gameState != null) {
            currentGameState = gameState;
        }
    }

    public void update(float dt) {
        currentGameState.update(dt);
    }

    public void render() {
        currentGameState.render();
    }

    public void dispose() {
        currentGameState.dispose();
    }
}
