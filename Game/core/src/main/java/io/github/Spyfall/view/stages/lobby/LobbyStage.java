package io.github.Spyfall.view.stages.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.StageView;

public class LobbyStage extends StageView {
    private final LobbyController controller = LobbyController.getInstance();
    private final GameModel gameModel = GameModel.getInstance();
    private final Skin skin;
    private final LobbyTable lobbyTable;

    public LobbyStage(ScreenViewport viewport) {
        super(viewport);

        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));
        lobbyTable = new LobbyTable(skin, controller, gameModel, stage);
        stage.addActor(lobbyTable);

        Gdx.input.setInputProcessor(stage);
    }

    public void updateFromModel() {
        lobbyTable.updateFromModel(gameModel);
    }

    @Override
    public void update() {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
