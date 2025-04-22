package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyPlayersTable extends Table {
    private final Table innerTable;
    private final ScrollPane scrollPane;

    public LobbyPlayersTable(Skin skin, GameModel gameModel, float W, float H) {
        super(skin);
        innerTable = new Table(skin);
        scrollPane = new ScrollPane(innerTable, skin);
        scrollPane.setFadeScrollBars(false);

        add(new Label("Players", skin)).padBottom(H * UIConstants.VERTICAL_GAP_PERCENT).row();
        add(scrollPane)
                .width(W * UIConstants.SCROLL_WIDTH_PERCENT)
                .height(H * UIConstants.SCROLL_HEIGHT_PERCENT)
                .padBottom(H * UIConstants.VERTICAL_GAP_PERCENT);

        update(gameModel);
    }

    public void update(GameModel gameModel) {
        innerTable.clear();
        for (String playerName : gameModel.getLobbyData().getPlayers()) {
            Label label = new Label(playerName, getSkin());
            if (playerName.equals(gameModel.getLobbyData().getHostPlayer())) {
                label.setText(playerName + " (Host)");
            }
            innerTable.add(label)
                    .padBottom(Gdx.graphics.getHeight() * (UIConstants.VERTICAL_GAP_PERCENT / 2f))
                    .row();
        }
    }
}
