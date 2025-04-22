
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.Spyfall.model.GameModel;

public class LobbyInfoTable extends Table {

    // Layout constants
    private final float TITLE_FONT_SCALE = 1.5f;
    private final float TITLE_SPACING = 30f;
    private final float LABEL_SPACING = 20f;
    private final float SMALL_LABEL_SPACING = 12f;

    private final Label lobbyCodeLabel;
    private final Label hostLabel;

    public LobbyInfoTable(Skin skin, GameModel gameModel) {
        super(skin);

        // Title
        Label titleLabel = new Label("Game Lobby", skin);
        titleLabel.setFontScale(TITLE_FONT_SCALE);

        // Labels
        lobbyCodeLabel = new Label("Lobby Code: " + gameModel.getLobbyCode(), skin);
        hostLabel = new Label("Host: " + gameModel.getLobbyData().getHostPlayer(), skin);

        // Layout
        add(titleLabel).padBottom(TITLE_SPACING).row();
        add(lobbyCodeLabel).padBottom(SMALL_LABEL_SPACING).row();
        add(hostLabel).padBottom(LABEL_SPACING).row();
    }

    // Update label values dynamically
    public void update(GameModel gameModel) {
        lobbyCodeLabel.setText("Lobby Code: " + gameModel.getLobbyCode());
        hostLabel.setText("Host: " + gameModel.getLobbyData().getHostPlayer());
    }
}
