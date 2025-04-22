
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.Spyfall.model.GameModel;

public class LobbyPlayersTable extends Table {

    // Layout constants
    private final float TABLE_WIDTH = 300f;
    private final float TABLE_HEIGHT = 200f;
    private final float SECTION_SPACING = 20f;
    private final float PLAYER_SPACING = 10f;
    private final float HEADER_FONT_SCALE = 1.3f;

    private final Table innerTable;
    private final ScrollPane scrollPane;

    public LobbyPlayersTable(Skin skin, GameModel gameModel) {
        super(skin);

        // Inner scrollable table
        innerTable = new Table(skin);
        scrollPane = new ScrollPane(innerTable, skin);
        scrollPane.setFadeScrollBars(false);

        // Section title
        Label title = new Label("Players", skin);
        title.setFontScale(HEADER_FONT_SCALE);

        add(title).padBottom(SECTION_SPACING).row();
        add(scrollPane)
                .width(TABLE_WIDTH)
                .height(TABLE_HEIGHT)
                .padBottom(SECTION_SPACING);

        update(gameModel);
    }

    public void update(GameModel gameModel) {
        innerTable.clear();

        for (String playerName : gameModel.getLobbyData().getPlayers()) {
            String labelText = playerName.equals(gameModel.getLobbyData().getHostPlayer())
                    ? playerName + " (Host)"
                    : playerName;

            Label label = new Label(labelText, getSkin());
            innerTable.add(label).padBottom(PLAYER_SPACING).row();
        }
    }
}
