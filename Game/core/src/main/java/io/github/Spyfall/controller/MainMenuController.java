package io.github.Spyfall.controller;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.services.SendMessageService;

public class MainMenuController {
    private final StageManager stageManager;
    private final SendMessageService sendMessageService;

    public MainMenuController() {
        this.stageManager = StageManager.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
    }

    public void onCreateGame(ScreenViewport viewport) {
        // TODO
    }

    public void onJoinLobby(String lobbyCode, String username) {

    }
}
