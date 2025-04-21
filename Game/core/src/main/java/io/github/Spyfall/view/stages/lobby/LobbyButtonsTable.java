
package io.github.Spyfall.view.stages.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyButtonsTable extends Table {

    private final TextButton startGameButton;
    private final TextButton leaveLobbyButton;
    private final TextButton editLocationsButton;
    private final TextButton editGameSettingsButton;
    private final Stage stage;

    public LobbyButtonsTable(Skin skin, LobbyController controller, Stage stage, GameModel gameModel) {
        super(skin);
        this.stage = stage;

        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);
        editLocationsButton = new TextButton("Edit Locations", skin);
        editGameSettingsButton = new TextButton("Edit Game Settings", skin);

        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);

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

        editLocationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Edit Locations");
                new LocationsEditorDialog(skin).show(stage);
            }
        });

        editGameSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new GameSettingsDialog(skin, gameModel.getUsername(), gameModel.getLobbyCode()).show(stage);
            }
        });

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        Table hostOnlyButtons = new Table(skin);
        hostOnlyButtons.add(startGameButton)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .padBottom(H * UIConstants.VERTICAL_GAP_PERCENT)
                .row();
        hostOnlyButtons.add(editLocationsButton)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .padBottom(H * UIConstants.VERTICAL_GAP_PERCENT)
                .row();
        hostOnlyButtons.add(editGameSettingsButton)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .padBottom(H * UIConstants.VERTICAL_GAP_PERCENT)
                .row();

        Table allButtons = new Table(skin);
        allButtons.add(hostOnlyButtons).row();
        allButtons.add(leaveLobbyButton)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this));

        add(allButtons);
    }

    public void updateVisibility(GameModel gameModel) {
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);
    }
}
