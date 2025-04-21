
package io.github.Spyfall.view.stages.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyButtonsTable extends Table {

    private final TextButton startGameButton;
    private final TextButton leaveLobbyButton;

    public LobbyButtonsTable(Skin skin, LobbyController controller, GameModel gameModel) {
        super(skin);

        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);

        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startGame();
            }
        });

        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.leaveLobby();
            }
        });

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        if (W < 500) {
            add(startGameButton)
                    .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                    .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                    .padBottom(H * UIConstants.VERTICAL_GAP_PERCENT)
                    .row();
            add(leaveLobbyButton)
                    .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                    .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this));
        } else {
            add(startGameButton)
                    .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT / 2f, this))
                    .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                    .padRight(W * 0.02f);
            add(leaveLobbyButton)
                    .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT / 2f, this))
                    .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this));
        }
    }

    public void updateVisibility(GameModel gameModel) {
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
    }
}
