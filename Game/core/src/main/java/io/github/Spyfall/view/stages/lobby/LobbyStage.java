package io.github.Spyfall.view.stages.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.ui.SettingsIcon;
import io.github.Spyfall.services.AudioService;

public class LobbyStage extends StageView {
    private final LobbyController controller = LobbyController.getInstance();
    private final GameModel gameModel = GameModel.getInstance();
    private final Skin skin;
    private final LobbyTable lobbyTable;
    private final SettingsIcon settingsIcon;

    public LobbyStage(ScreenViewport viewport) {
        super(viewport);

        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Add main UI
        lobbyTable = new LobbyTable(skin, controller, gameModel, stage);
        stage.addActor(lobbyTable);

        // Add floating settings icon
        settingsIcon = new SettingsIcon(skin, AudioService.getInstance(), stage);
        stage.addActor(settingsIcon);

        // Position it in bottom-right corner (after stage size is valid)
        Gdx.app.postRunnable(() -> {
            float x = viewport.getWorldWidth() - settingsIcon.getWidth() - 20f;
            float y = 20f;
            settingsIcon.setPosition(x, y);
        });

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

        // Reposition settings icon on resize
        float x = viewport.getWorldWidth() - settingsIcon.getWidth() - 20f;
        float y = 20f;
        settingsIcon.setPosition(x, y);
    }

    public void dispose() {
        stage.dispose();
    }
}
