
package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.client.AssetLoader;
import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.game.ui.PlayerInfo;
import io.github.Spyfall.view.game.ui.RoundEndOverlay;
import io.github.Spyfall.view.game.ui.RoundEndOverlay.RoundEndListener;
import io.github.Spyfall.view.game.ui.Timer;
import io.github.Spyfall.view.game.ui.Timer.TimerListener;

public abstract class BaseGameStage extends StageView implements TimerListener, RoundEndListener {

    // Constants for layout and UI sizing
    protected final float ROUND_END_MAX_WIDTH = 500f;
    protected final float ROUND_END_WIDTH_PERCENT = 0.85f;

    // Core references
    protected Skin skin;
    protected GameplayController controller;
    protected GameModel gameModel;

    // UI components
    protected Table rootTable;
    protected Texture bgTexture;
    protected Timer timerComponent;
    protected PlayerInfo playerInfoComponent;
    protected RoundEndOverlay roundEndOverlay;

    // Round state flag
    protected boolean isRoundEnded = false;

    public BaseGameStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = GameplayController.getInstance();
        this.gameModel = GameModel.getInstance();
    }

    /**
     * Initializes all core UI components and layout.
     */
    protected void init() {
        bgTexture = AssetLoader.mainBackground;
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        rootTable = new Table();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        rootTable.setFillParent(true);

        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());

        timerComponent = new Timer(skin);
        timerComponent.setListener(this);

        playerInfoComponent = new PlayerInfo(skin);

        float contentWidth = Math.min(ROUND_END_MAX_WIDTH, Gdx.graphics.getWidth() * ROUND_END_WIDTH_PERCENT);

        roundEndOverlay = new RoundEndOverlay(skin, gameModel.getUsername(), isHost, contentWidth);
        roundEndOverlay.setListener(this);
        roundEndOverlay.getActor().setVisible(false);

        stage.addActor(rootTable);
        stage.addActor(roundEndOverlay.getActor());
    }

    @Override
    public void update() {
        // Update relevant components every frame
        if (timerComponent != null)
            timerComponent.update();
        if (playerInfoComponent != null)
            playerInfoComponent.update();
        if (roundEndOverlay != null && isRoundEnded)
            roundEndOverlay.update();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void startTimer(float seconds) {
        if (timerComponent != null)
            timerComponent.start(seconds);
    }

    public void stopTimer() {
        if (timerComponent != null)
            timerComponent.stop();
    }

    public void updateTimerDisplay(float seconds) {
        if (timerComponent != null) {
            timerComponent.setText(
                    seconds <= 0 ? "00:00" : String.format("%02d:%02d", (int) (seconds / 60), (int) (seconds % 60)));
        }
    }

    @Override
    public void onTimerEnd() {
        // Should be implemented by subclasses
    }

    @Override
    public void onNextRoundClicked() {
        resetRoundEndUI();
        controller.onStartNewRound();
    }

    /**
     * Triggers end-of-round UI with data from the model.
     */
    public void handleRoundEnded(int roundNumber, String reason, String spy,
            String location, HashMap<String, Integer> scoreboard) {
        isRoundEnded = true;

        if (timerComponent != null) {
            timerComponent.stop();
            timerComponent.setText("ROUND ENDED");
        }

        if (roundEndOverlay != null) {
            HashMap<String, Integer> safeScoreboard = scoreboard != null ? scoreboard : new HashMap<>();
            roundEndOverlay.setRoundEndData(roundNumber, reason, spy, location, safeScoreboard);
            roundEndOverlay.getActor().setVisible(true);
        }
    }

    /**
     * Hides end-of-round overlay and resets state.
     */
    public void resetRoundEndUI() {
        if (roundEndOverlay != null) {
            roundEndOverlay.getActor().setVisible(false);
        }
        isRoundEnded = false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        if (bgTexture != null)
            bgTexture.dispose();
        if (roundEndOverlay != null)
            roundEndOverlay.dispose();
    }
}
