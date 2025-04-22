
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.ui.SettingsIcon;

public class LobbyStage extends StageView {

    // Layout constants
    private final float SETTINGS_MARGIN_X = 20f;
    private final float SETTINGS_MARGIN_Y = 20f;

    private final LobbyController controller = LobbyController.getInstance();
    private final GameModel gameModel = GameModel.getInstance();
    private final Skin skin;
    private final LobbyTable lobbyTable;
    private final SettingsIcon settingsIcon;

    public LobbyStage(ScreenViewport viewport) {
        super(viewport);

        // Load skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Reset music on enter
        AudioService.getInstance().playMusic("background", true);

        // Add lobby layout
        lobbyTable = new LobbyTable(skin, controller, gameModel, stage);
        stage.addActor(lobbyTable);

        // Add settings icon overlay
        settingsIcon = new SettingsIcon(skin, AudioService.getInstance(), stage);
        stage.addActor(settingsIcon);

        // Position settings icon
        Gdx.app.postRunnable(() -> {
            float x = viewport.getWorldWidth() - settingsIcon.getWidth() - SETTINGS_MARGIN_X;
            float y = SETTINGS_MARGIN_Y;
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

        // Reposition settings icon after resize
        float x = viewport.getWorldWidth() - settingsIcon.getWidth() - SETTINGS_MARGIN_X;
        float y = SETTINGS_MARGIN_Y;
        settingsIcon.setPosition(x, y);
    }

    public void dispose() {
        stage.dispose();
    }
}
