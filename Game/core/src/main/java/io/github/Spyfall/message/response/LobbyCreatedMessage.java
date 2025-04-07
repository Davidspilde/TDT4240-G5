package io.github.Spyfall.message.response;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.view.GameLobby;
import io.github.Spyfall.controller.StageManager;

public class LobbyCreatedMessage extends ResponseMessage {
    private String lobbyCode;
    private String host;

    public LobbyCreatedMessage() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public void handleLobbyCreated() {
        System.out.println("Transitioning to GameLobby stage");
        ScreenViewport viewport = new ScreenViewport();
        GameLobby gameLobby = new GameLobby(lobbyCode, host, host, viewport);
        StageManager.getInstance().setStage(gameLobby);
    }
}
