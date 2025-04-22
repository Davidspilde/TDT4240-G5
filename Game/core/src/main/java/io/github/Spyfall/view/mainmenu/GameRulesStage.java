package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.view.StageView;

public class GameRulesStage extends StageView {
    private final MainMenuController controller;
    private final Skin skin;
    private final Table rootTable;
    private int currentPage = 0;
    private final int totalPages = 7;
    private final Label pageLabel;
    private final Label titleLabel;
    private final Label contentLabel;
    private final TextButton prevButton;
    private final TextButton nextButton;
    private final TextButton backButton;
    private final GameModel gameModel;

    public GameRulesStage(MainMenuController controller) {
        super(new ScreenViewport());
        this.controller = controller;
        this.gameModel = GameModel.getInstance();
        this.skin = new Skin(
                Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create root table
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Create labels for content
        titleLabel = new Label("", skin, "default");
        titleLabel.setFontScale(1.2f);
        contentLabel = new Label("", skin, "default");
        contentLabel.setWrap(true);
        pageLabel = new Label("", skin, "default");

        // Create navigation buttons
        prevButton = new TextButton("Prev", skin);
        nextButton = new TextButton("Next", skin);
        backButton = new TextButton("Back", skin);

        // Add button listeners with debug output
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Prev button clicked");
                if (currentPage > 0) {
                    currentPage--;
                    updateContent();
                }
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Next button clicked");
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    updateContent();
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Back button clicked");
                gameModel.setCurrentState(GameState.MAIN_MENU);
            }
        });

        // Layout
        rootTable.add(titleLabel).padBottom(10).row();
        rootTable.add(contentLabel).width(Gdx.graphics.getWidth() - 100).padBottom(20).row();
        rootTable.add(pageLabel).padBottom(10).row();

        // Create button table with proper spacing
        Table buttonTable = new Table();
        buttonTable.defaults().pad(5).width(100).height(40);
        buttonTable.add(prevButton);
        buttonTable.add(nextButton);
        buttonTable.add(backButton);

        rootTable.add(buttonTable).pad(10);

        // Initialize content
        updateContent();

        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }

    private void updateContent() {
        // Reset font scale to default
        contentLabel.setFontScale(1.0f);

        switch (currentPage) {
            case 0:
                titleLabel.setText("Background");
                contentLabel.setText(
                        "You are a spy in a foreign country. Your mission is to gather intelligence about a secret meeting while avoiding detection.");
                break;
            case 1:
                titleLabel.setText("Objective");
                contentLabel.setText(
                        "As a spy, you must identify the location of the secret meeting.\n\nAs a regular player, you must identify the spy.");
                break;
            case 2:
                titleLabel.setText("Game Flow - Setup");
                contentLabel
                        .setText(
                                "1. Each player receives a location with an associated role, except the spy who knows nothing\n\n"
                                        +
                                        "2. Players take turns asking questions about the location\n\n");
                break;
            case 3:
                titleLabel.setText("Game Flow - Actions (1/2)");
                contentLabel.setText("During the game:\n\n" +
                        "1. At any time, regular players can vote on who they think is the spy\n\n" +
                        "2. At any time, the spy can guess the location");
                break;
            case 4:
                titleLabel.setText("Game Flow - Actions (2/2)");
                contentLabel.setText("3. If the spy is caught, they get one final chance to guess the location\n\n" +
                        "The game ends when:\n" +
                        "- The spy is correctly identified and they guess the wrong location(Player win)\n" +
                        "- The spy correctly guesses the location (spy wins)");
                break;
            case 5:
                titleLabel.setText("Win Condition - Regular Players");
                contentLabel.setText("As a regular player, you can win by:\n\n" +
                        "✓ Correctly identifying the spy while hiding the location");
                break;
            case 6:
                titleLabel.setText("Win Condition - Spy");
                contentLabel.setText("As the spy, you win by:\n\n" +
                        "✓ Correctly guessing the location\n\n" +
                        "✓ Successfully avoiding detection");
                break;
        }
        pageLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages));

        // Update button states
        prevButton.setDisabled(currentPage == 0);
        nextButton.setDisabled(currentPage == totalPages - 1);
    }

    @Override
    public void update() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
