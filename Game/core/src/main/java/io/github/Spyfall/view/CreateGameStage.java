package io.github.Spyfall.view;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.LobbyController;

public class CreateGameStage extends StageView {
    private LobbyController controller;
    public CreateGameStage(ScreenViewport viewport, LobbyController lobbyController) {
        super(viewport);
        this.controller = lobbyController;
        initStage();
    }

    public void initStage(){
        // TODO:
    }
}
