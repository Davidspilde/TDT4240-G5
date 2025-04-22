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
import io.github.Spyfall.view.game.ui.GameControls;
import io.github.Spyfall.view.game.ui.GameControls.GameControlListener;
import io.github.Spyfall.view.game.ui.PlayerInfo;
import io.github.Spyfall.view.game.ui.RoundEndOverlay;
import io.github.Spyfall.view.game.ui.RoundEndOverlay.RoundEndListener;
import io.github.Spyfall.view.game.ui.Timer;
import io.github.Spyfall.view.game.ui.Timer.TimerListener;

public abstract class BaseGameStage extends StageView implements TimerListener, GameControlListener, RoundEndListener {

    protected Skin skin;
    protected GameplayController controller;
    protected GameModel gameModel;

    // UI Components
    protected Table rootTable;
    protected Texture bgTexture;
    protected Timer timerComponent;
    protected PlayerInfo playerInfoComponent;
    protected GameControls gameControlsComponent;
    protected RoundEndOverlay roundEndOverlay;

    // isRoundEnded
    protected boolean isRoundEnded = false;

    public BaseGameStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = GameplayController.getInstance();
        this.gameModel = GameModel.getInstance();
    }

    protected void init() {
        // init
        bgTexture = AssetLoader.mainBackground;
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // root table
        rootTable = new Table();
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);
        rootTable.setFillParent(true);

        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());

        timerComponent = new Timer(skin);
        timerComponent.setListener(this);

        playerInfoComponent = new PlayerInfo(skin);

        gameControlsComponent = new GameControls(skin, isHost);
        gameControlsComponent.setListener(this);

        float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
        roundEndOverlay = new RoundEndOverlay(
                skin,
                gameModel.getUsername(),
                isHost,
                contentWidth);
        roundEndOverlay.setListener(this);
        roundEndOverlay.getActor().setVisible(false);

        stage.addActor(rootTable);

        stage.addActor(roundEndOverlay.getActor());
    }

    @Override
    public void update() {
        // Update components
        if (timerComponent != null) {
            timerComponent.update();
        }

        if (playerInfoComponent != null) {
            playerInfoComponent.update();
        }

        if (gameControlsComponent != null) {
            gameControlsComponent.update();
        }

        if (roundEndOverlay != null && isRoundEnded) {
            roundEndOverlay.update();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void startTimer(float seconds) {
        if (timerComponent != null) {
            timerComponent.start(seconds);
        }
    }

    /**
     * Stop the timer
     */
    public void stopTimer() {
        if (timerComponent != null) {
            timerComponent.stop();
        }
    }

    /**
     * Update the timer display with a specific time value
     *
     * @param seconds Time in seconds
     */
    public void updateTimerDisplay(float seconds) {
        if (timerComponent != null) {
            timerComponent.setText(
                    seconds <= 0 ? "00:00" : String.format("%02d:%02d", (int) (seconds / 60), (int) (seconds % 60)));
        }
    }

    @Override
    public void onTimerEnd() {
        // subclasses should implement
    }

    @Override
    public void onEndGameClicked() {
        // TODO
        // controller.endGame();
    }

    @Override
    public void onNextRoundClicked() {
        resetRoundEndUI();
        controller.onStartNewRound();
    }

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
        if (bgTexture != null) {
            bgTexture.dispose();
        }

        if (roundEndOverlay != null) {
            roundEndOverlay.dispose();
        }
    }
}
