
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.services.AudioService;

public class LobbyButtonsTable extends Table {

    // Layout constants
    private final float BUTTON_WIDTH = Gdx.graphics.getWidth() * 0.4f;
    private final float BUTTON_HEIGHT = Gdx.graphics.getHeight() * 0.08f;
    private final float BUTTON_FONT_SCALE = 1.2f;
    private final float BUTTON_GAP = Gdx.graphics.getHeight() * 0.02f;

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

        // Create buttons
        startGameButton = createButton("Start Game", skin);
        leaveLobbyButton = createButton("Leave Lobby", skin);
        editLocationsButton = createButton("Edit Locations", skin);
        editGameSettingsButton = createButton("Game Settings", skin);

        // Determine if user is host
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);

        // Button listeners
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

        // Layout setup
        Table hostOnlyButtons = new Table(skin);
        hostOnlyButtons.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_GAP);
        hostOnlyButtons.add(startGameButton).row();
        hostOnlyButtons.add(editLocationsButton).row();
        hostOnlyButtons.add(editGameSettingsButton).row();

        Table allButtons = new Table(skin);
        allButtons.add(hostOnlyButtons).row();
        allButtons.add(leaveLobbyButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padTop(BUTTON_GAP);

        add(allButtons).expand().center();
    }

    // Helper to apply consistent button styling
    private TextButton createButton(String text, Skin skin) {
        TextButton btn = new TextButton(text, skin);
        btn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        return btn;
    }

    // Toggle host-only button visibility
    public void updateVisibility(GameModel gameModel) {
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        editLocationsButton.setVisible(isHost);
        editGameSettingsButton.setVisible(isHost);
    }
}
