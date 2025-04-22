
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.ui.UIConstants;

public class LobbyButtonsTable extends Table {

    private final TextButton startGameButton;
    private final TextButton leaveLobbyButton;
    private final TextButton editLocationsButton;
    private final TextButton editGameSettingsButton;
    private final AudioService audioService;
    private final Stage stage;

    public LobbyButtonsTable(Skin skin, LobbyController controller, Stage stage, GameModel gameModel,
            AudioService audioService) {
        super(skin);
        this.stage = stage;
        this.audioService = audioService;

        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);
        editLocationsButton = new TextButton("Edit Locations", skin);
        editGameSettingsButton = new TextButton("Game Settings", skin);

        // Bigger font for emphasis
        startGameButton.getLabel().setFontScale(1.2f);
        leaveLobbyButton.getLabel().setFontScale(1.2f);
        editLocationsButton.getLabel().setFontScale(1.2f);
        editGameSettingsButton.getLabel().setFontScale(1.2f);

        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                controller.startGame();
            }
        });

        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                controller.leaveLobby();
            }
        });

        editLocationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                new LocationsEditorDialog(skin, controller, stage, audioService).show(stage);
            }
        });

        editGameSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                new GameSettingsDialog(skin, controller, stage, audioService).show(stage);
            }
        });

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        float buttonWidth = W * 0.4f;
        float buttonHeight = H * 0.08f;
        float gap = H * 0.02f;

        Table hostOnlyButtons = new Table(skin);
        hostOnlyButtons.defaults().width(buttonWidth).height(buttonHeight).padBottom(gap);
        hostOnlyButtons.add(startGameButton).row();
        hostOnlyButtons.add(editLocationsButton).row();
        hostOnlyButtons.add(editGameSettingsButton).row();

        Table allButtons = new Table(skin);
        allButtons.add(hostOnlyButtons).row();
        allButtons.add(leaveLobbyButton).width(buttonWidth).height(buttonHeight).padTop(gap);

        add(allButtons).expand().center();
    }

    public void updateVisibility(GameModel gameModel) {
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);
    }
}
