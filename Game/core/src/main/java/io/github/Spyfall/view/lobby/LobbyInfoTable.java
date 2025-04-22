package io.github.Spyfall.view.stages.lobby;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyInfoTable extends Table {
    private final Label lobbyCodeLabel;
    private final Label hostLabel;

    public LobbyInfoTable(Skin skin, GameModel gameModel, float H) {
        super(skin);

        Label titleLabel = new Label("Game Lobby", skin);
        titleLabel.setFontScale(1.5f);

        lobbyCodeLabel = new Label("Lobby Code: " + gameModel.getLobbyCode(), skin);
        hostLabel = new Label("Host: " + gameModel.getLobbyData().getHostPlayer(), skin);

        add(titleLabel).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT).row();
        add(lobbyCodeLabel).padBottom(H * (UIConstants.VERTICAL_GAP_PERCENT / 2f)).row();
        add(hostLabel).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT).row();
    }

    public void update(GameModel gameModel) {
        lobbyCodeLabel.setText("Lobby Code: " + gameModel.getLobbyCode());
        hostLabel.setText("Host: " + gameModel.getLobbyData().getHostPlayer());
    }
}
