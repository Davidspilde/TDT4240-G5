package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.Spyfall.client.AssetLoader;
import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyTable extends Table {

    private final LobbyInfoTable lobbyInfoTable;
    private final LobbyPlayersTable playersTable;
    private final LobbyButtonsTable buttonsTable;

    public LobbyTable(Skin skin, LobbyController controller, GameModel gameModel, Stage stage) {
        super(skin);
        setFillParent(true);
        top();

        setBackground(new TextureRegionDrawable(new TextureRegion(AssetLoader.mainBackground)));

        float W = stage.getViewport().getWorldWidth();
        float H = stage.getViewport().getWorldHeight();

        // Subcomponents
        lobbyInfoTable = new LobbyInfoTable(skin, gameModel, H);
        playersTable = new LobbyPlayersTable(skin, gameModel, W, H);
        buttonsTable = new LobbyButtonsTable(skin, controller, stage, gameModel, AudioService.getInstance());

        // Layout
        add(lobbyInfoTable).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT).row();
        add(playersTable).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT).row();
        add(buttonsTable).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT);
    }

    public void updateFromModel(GameModel gameModel) {
        lobbyInfoTable.update(gameModel);
        playersTable.update(gameModel);
        buttonsTable.updateVisibility(gameModel);
    }
}
