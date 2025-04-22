package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.view.game.ui.PlayersList;

public class PlayerGameStage extends BaseGameStage {

    private PlayersList playersListComponent;
    private GameplayController controller;

    public PlayerGameStage(String locationName, String roleName, ScreenViewport viewport) {
        super(viewport);
        System.out.println("CREATING PlayerGameStage: location=" + locationName + ", role=" + roleName);

        controller = GameplayController.getInstance();
        initStage(locationName, roleName);

        System.out.println("PlayerGameStage initialized with " + getStage().getActors().size + " root actors");
    }

    protected void initStage(String locationName, String roleName) {
        super.init();

        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation(locationName);

        playersListComponent = new PlayersList(skin, gameModel.getUsername(), controller);
        playersListComponent.setPlayers(gameModel.getLobbyData().getPlayers());

        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerComponent.getActor());
        infoGroup.addActor(playerInfoComponent.getActor());

        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(playersListComponent.getActor()).expand().fill();

        rootTable.row().padTop(20);
        rootTable.add(gameControlsComponent.getActor());
    }

    @Override
    public void update() {
        super.update();

        // update other components if needed
    }

    @Override
    public void onTimerEnd() {
        if (roundEndOverlay != null) {
            roundEndOverlay.getActor().setVisible(true);
            isRoundEnded = true;
        }
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy,
            String location, HashMap<String, Integer> scoreboard) {
        super.handleRoundEnded(roundNumber, reason, spy, location, scoreboard);
    }
}
