
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
    private PlayersList playersListComponent;
    private LocationsListComponent locationsComponent;
    private Label spyRevealLabel;

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

        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation(isSpy ? "???" : locationName);

        playersListComponent = new PlayersList(skin, gameModel.getUsername(), controller);
        playersListComponent.setPlayers(gameModel.getLobbyData().getPlayers());

        if (isSpy) {
            locationsComponent = new LocationsListComponent(skin, controller);
            locationsComponent.setLocations(gameModel.getGameData().getPossibleLocations());
            locationsComponent.setGreyedOutLocations(gameModel.getGameData().getGreyedOutLocations());
        }

        spyRevealLabel = new Label("", skin);
        spyRevealLabel.setFontScale(1.2f);
        spyRevealLabel.getColor().a = 0f; // invisible initially

        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerComponent.getActor());
        infoGroup.addActor(playerInfoComponent.getActor());

        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(playersListComponent.getActor()).expand().fill().row();

        if (isSpy) {
            rootTable.row().padTop(20);
            rootTable.add(locationsComponent.getActor()).expand().fill().row();
        }

        rootTable.row().padTop(20);
        rootTable.add(spyRevealLabel).center().row();

        rootTable.row().padTop(20);
        rootTable.add(gameControlsComponent.getActor());

        // Add floating settings icon
        SettingsIcon settingsIcon = new SettingsIcon(skin, AudioService.getInstance(), stage);
        stage.addActor(settingsIcon);

        // Position it in bottom-right corner (after stage size is valid)
        Gdx.app.postRunnable(() -> {
            float x = viewport.getWorldWidth() - settingsIcon.getWidth() - 20f;
            float y = 20f;
            settingsIcon.setPosition(x, y);
        });

    }

    @Override
    public void update() {
        super.update();
    }

    public void handleNewRound() {
        if (isSpy && locationsComponent != null) {
            GameData gameData = gameModel.getGameData();
            locationsComponent.setLocations(gameData.getPossibleLocations());
            locationsComponent.setGreyedOutLocations(gameData.getGreyedOutLocations());
        }

        // Reset reveal label
        if (spyRevealLabel != null) {
            spyRevealLabel.clearActions();
            spyRevealLabel.getColor().a = 0f;
            spyRevealLabel.setText("");
        }
    }

    public void showSpyReveal(String spyUsername) {
        if (spyRevealLabel != null) {
            if (!isSpy) {
                spyRevealLabel.setText(" The spy is " + spyUsername + "!");
            } else {
                spyRevealLabel.setText("You get one last attempt!");
            }
            spyRevealLabel.getColor().a = 0f; // reset alpha
            spyRevealLabel.addAction(Actions.fadeIn(1f)); // fade in over 1 second
        }
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy,
            String location, HashMap<String, Integer> scoreboard) {
        super.handleRoundEnded(roundNumber, reason, spy, location, scoreboard);
        // Optionally reset or update other UI here
    }
}
