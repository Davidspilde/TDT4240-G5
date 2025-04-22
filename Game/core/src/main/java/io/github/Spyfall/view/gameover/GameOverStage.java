
package io.github.Spyfall.view.gameover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.controller.MainController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.StageView;

public class GameOverStage extends StageView {

    private final Skin skin;
    private final GameplayController controller;
    private final GameModel gameModel;

    private final Table rootTable;

    public GameOverStage(ScreenViewport viewport, HashMap<String, Integer> scoreboard) {
        super(viewport);

        this.skin = new Skin(
                Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));
        this.controller = GameplayController.getInstance();
        this.gameModel = GameModel.getInstance();

        rootTable = new Table();
        rootTable.setFillParent(true);

        Texture bgTexture = new Texture(Gdx.files.internal("Background_city.png"));
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(bgTexture));
        rootTable.setBackground(bgDrawable);

        stage.addActor(rootTable);

        buildUI(scoreboard);
    }

    private void buildUI(HashMap<String, Integer> scoreboard) {
        rootTable.top().pad(30);

        Label title = new Label("Game Over!", skin);
        title.setFontScale(1.5f);
        rootTable.add(title).colspan(3).center().padBottom(40).row();

        // Sort by score descending
        LinkedHashMap<String, Integer> sortedScores = scoreboard.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        String[] topPlayers = sortedScores.keySet().toArray(new String[0]);

        Table podiumTable = new Table();

        // Podium (second - first - third)
        for (int i = 0; i < 3; i++) {
            String name = (i < topPlayers.length) ? topPlayers[i] : "-";
            int score = (i < topPlayers.length) ? sortedScores.get(name) : 0;

            Label label = new Label(name + "\n" + score, skin);
            label.setAlignment(1);
            label.setFontScale(i == 1 ? 1.3f : 1.0f);
            float height = i == 1 ? 180 : (i == 0 ? 120 : 100);
            Table block = new Table();
            block.setBackground("default-round");
            block.add(label).pad(10);

            podiumTable.add(block).width(100).height(height).pad(20);
        }

        rootTable.add(podiumTable).colspan(3).center().padBottom(40).row();

        Table buttonTable = new Table();
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());

        if (isHost) {
            TextButton newGameBtn = new TextButton("Start New Game", skin);
            newGameBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.onStartNewRound();
                }
            });

            TextButton backToLobbyBtn = new TextButton("Back to Lobby", skin);
            backToLobbyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MainController.getInstance().setLobbyStage();
                }
            });

            buttonTable.add(newGameBtn).pad(10);
            buttonTable.add(backToLobbyBtn).pad(10);

        } else {
            TextButton leaveBtn = new TextButton("Leave Lobby", skin);
            leaveBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    LobbyController.getInstance().leaveLobby();
                }
            });

            buttonTable.add(leaveBtn).pad(10);
        }

        rootTable.add(buttonTable).colspan(3).center();
    }

    @Override
    public void update() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

}
