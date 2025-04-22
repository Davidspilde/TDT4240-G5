
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

public class LobbyTable extends Table {

    // Layout constants
    private final float GAP = 30f;

    private final LobbyInfoTable lobbyInfoTable;
    private final LobbyPlayersTable playersTable;
    private final LobbyButtonsTable buttonsTable;

    public LobbyTable(Skin skin, LobbyController controller, GameModel gameModel, Stage stage) {
        super(skin);
        setFillParent(true);
        top();

        // Set background image
        setBackground(new TextureRegionDrawable(new TextureRegion(AssetLoader.mainBackground)));

        // Initialize UI components
        lobbyInfoTable = new LobbyInfoTable(skin, gameModel);
        playersTable = new LobbyPlayersTable(skin, gameModel);
        buttonsTable = new LobbyButtonsTable(skin, controller, stage, gameModel, AudioService.getInstance());

        // Layout arrangement
        add(lobbyInfoTable).padBottom(GAP).row();
        add(playersTable).padBottom(GAP).row();
        add(buttonsTable).padBottom(GAP);
    }

    public void updateFromModel(GameModel gameModel) {
        lobbyInfoTable.update(gameModel);
        playersTable.update(gameModel);
        buttonsTable.updateVisibility(gameModel);
    }
}
