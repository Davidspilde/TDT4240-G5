
package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameData;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.game.ui.LocationsListComponent;
import io.github.Spyfall.view.game.ui.PlayersList;
import io.github.Spyfall.view.ui.SettingsIcon;

public class GameStage extends BaseGameStage {

    // Layout constants
    private final float PADDING_TOP = 20f;
    private final float COMPONENT_SPACING = 10f;
    private final float LABEL_FONT_SCALE = 1.2f;
    private final float SETTINGS_ICON_MARGIN = 20f;

    // Components
    private PlayersList playersListComponent;
    private LocationsListComponent locationsComponent;
    private Label spyRevealLabel;

    // Game context
    private final boolean isSpy;
    private final String roleName;
    private final String locationName;

    public GameStage(String roleName, String locationName, boolean isSpy, ScreenViewport viewport) {
        super(viewport);
        this.isSpy = isSpy;
        this.roleName = roleName;
        this.locationName = locationName;
        initStage();
    }

    private void initStage() {
        super.init();

        // Initialize info block
        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation(isSpy ? "???" : locationName);

        // Setup player list
        playersListComponent = new PlayersList(skin, gameModel.getUsername(), controller, AudioService.getInstance());
        playersListComponent.setPlayers(gameModel.getLobbyData().getPlayers());

        // Spy gets a list of possible locations
        if (isSpy) {
            locationsComponent = new LocationsListComponent(skin, controller, stage, AudioService.getInstance());
            locationsComponent.setLocations(gameModel.getGameData().getPossibleLocations());
        }

        // Label used to show end-of-round spy message
        spyRevealLabel = new Label("", skin);
        spyRevealLabel.setFontScale(LABEL_FONT_SCALE);
        spyRevealLabel.getColor().a = 0f;

        // Vertical group for timer and role/location info
        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(COMPONENT_SPACING);
        infoGroup.addActor(timerComponent.getActor());
        infoGroup.addActor(playerInfoComponent.getActor());

        // Layout components
        rootTable.top().pad(PADDING_TOP);
        rootTable.add(infoGroup).expandX().center().row();

        rootTable.row().padTop(PADDING_TOP);
        rootTable.add(playersListComponent.getActor()).expand().fill().row();

        if (isSpy) {
            rootTable.row().padTop(PADDING_TOP);
            rootTable.add(locationsComponent.getActor()).expand().fill().row();
        }

        rootTable.row().padTop(PADDING_TOP);
        rootTable.add(spyRevealLabel).center().row();

        // Floating settings icon
        SettingsIcon settingsIcon = new SettingsIcon(skin, AudioService.getInstance(), stage);
        stage.addActor(settingsIcon);

        // Position it in bottom-right after layout
        Gdx.app.postRunnable(() -> {
            float x = viewport.getWorldWidth() - settingsIcon.getWidth() - SETTINGS_ICON_MARGIN;
            float y = SETTINGS_ICON_MARGIN;
            settingsIcon.setPosition(x, y);
        });
    }

    @Override
    public void update() {
        super.update(); // Handle UI and timer updates
    }

    /**
     * Prepares UI for a new round.
     */
    public void handleNewRound() {
        if (isSpy && locationsComponent != null) {
            GameData gameData = gameModel.getGameData();
            locationsComponent.setLocations(gameData.getPossibleLocations());
        }

        // Reset spy reveal label
        if (spyRevealLabel != null) {
            spyRevealLabel.clearActions();
            spyRevealLabel.getColor().a = 0f;
            spyRevealLabel.setText("");
        }
    }

    /**
     * Displays a reveal message when the spy is revealed at the end of the round.
     */
    public void showSpyReveal(String spyUsername) {
        if (spyRevealLabel != null) {
            if (!isSpy) {
                spyRevealLabel.setText("The spy is " + spyUsername + "!");
            } else {
                spyRevealLabel.setText("You get one last attempt!");
            }
            spyRevealLabel.getColor().a = 0f;
            spyRevealLabel.addAction(Actions.fadeIn(1f)); // Fade in reveal
        }
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy,
            String location, HashMap<String, Integer> scoreboard) {
        super.handleRoundEnded(roundNumber, reason, spy, location, scoreboard);
        // Additional cleanup or animation could go here
    }
}
