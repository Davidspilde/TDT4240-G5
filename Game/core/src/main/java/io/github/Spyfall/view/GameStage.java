package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.services.SendMessageService;

import java.util.List;
import java.util.Map;

public class GameStage extends StageView {
    private Skin skin;
    private Stage stage;
    private String lobbyCode;
    private String username;
    private String role;
    private String location;
    private int roundNumber;
    private int roundDuration;
    private Map<String, Integer> scoreboard;

    // UI elements
    private Label roleLabel;
    private Label locationLabel;
    private Label roundLabel;
    private Label timerLabel;
    private Table playersTable;
    private TextButton voteButton;
    private TextButton guessLocationButton;
    private Table scoreboardTable;

    // The background texture
    private Texture bgTexture;

    public GameStage(String lobbyCode, String username, String role, String location, int roundNumber, int roundDuration, ScreenViewport viewport) {
        super(viewport);
        this.lobbyCode = lobbyCode;
        this.username = username;
        this.role = role;
        this.location = location;
        this.roundNumber = roundNumber;
        this.roundDuration = roundDuration;
        init();
    }

    private void init() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        initStage();
    }

    private void initStage() {
        System.out.println("Initializing GameStage");
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        roleLabel = new Label("Role: " + role, skin);
        locationLabel = new Label("Location: " + (role.equals("Spy") ? "???" : location), skin);
        roundLabel = new Label("Round " + roundNumber, skin);
        timerLabel = new Label("Time: " + roundDuration + "s", skin);
        voteButton = new TextButton("Vote", skin);
        guessLocationButton = new TextButton("Guess Location", skin);

        // Create players table
        playersTable = new Table(skin);
        playersTable.defaults().pad(5);
        Label playersLabel = new Label("Players:", skin);
        playersTable.add(playersLabel).row();

        // Create scoreboard table
        scoreboardTable = new Table(skin);
        scoreboardTable.defaults().pad(5);
        Label scoreLabel = new Label("Scoreboard:", skin);
        scoreboardTable.add(scoreLabel).row();

        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png"))));

        // Add game info
        Table infoTable = new Table(skin);
        infoTable.defaults().pad(5);
        infoTable.add(roundLabel).row();
        infoTable.add(timerLabel).row();
        infoTable.add(roleLabel).row();
        infoTable.add(locationLabel).row();

        rootTable.add(infoTable).expandX().center().padTop(20).row();

        // Create buttons table
        Table buttonsTable = new Table(skin);
        buttonsTable.defaults().pad(5);
        buttonsTable.add(voteButton);
        if (role.equals("Spy")) {
            buttonsTable.add(guessLocationButton);
        }

        rootTable.add(buttonsTable).expandX().center().padTop(20).row();

        // Add players and scoreboard tables side by side
        Table gameInfoTable = new Table(skin);
        gameInfoTable.defaults().pad(5);
        gameInfoTable.add(playersTable).expandX().fill();
        gameInfoTable.add(scoreboardTable).expandX().fill();

        rootTable.add(gameInfoTable).expand().fill().pad(20);

        // Add button listeners
        voteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showVoteDialog();
            }
        });

        guessLocationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (role.equals("Spy")) {
                    showGuessLocationDialog();
                }
            }
        });

        stage.addActor(rootTable);
        System.out.println("GameStage initialized");
    }

    private void showVoteDialog() {
        Dialog dialog = new Dialog("Vote for Spy", skin);
        final TextField targetField = new TextField("", skin);
        targetField.setMessageText("Enter player name");

        dialog.getContentTable().add(new Label("Who do you think is the spy?", skin)).pad(5).row();
        dialog.getContentTable().add(targetField).width(200).pad(5);

        dialog.button("Vote", true);
        dialog.button("Cancel", false);

        dialog.setModal(true);
        dialog.key(com.badlogic.gdx.Input.Keys.ENTER, true); // Makes pressing Enter select "Vote"
        dialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false); // Makes pressing Escape select "Cancel"

        dialog.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (actor instanceof TextButton) {
                    TextButton button = (TextButton) actor;
                    if (button.getText().equals("Vote")) {
                        String target = targetField.getText().trim();
                        if (!target.isEmpty()) {
                            SendMessageService.getInstance().vote(username, target, lobbyCode);
                        }
                    }
                    dialog.hide();
                }
            }
        });

        dialog.show(stage);
    }

    private void showGuessLocationDialog() {
        Dialog dialog = new Dialog("Guess Location", skin);
        final TextField locationField = new TextField("", skin);
        locationField.setMessageText("Enter location");

        dialog.getContentTable().add(new Label("What's the location?", skin)).pad(5).row();
        dialog.getContentTable().add(locationField).width(200).pad(5);

        dialog.button("Guess", true);
        dialog.button("Cancel", false);

        dialog.setModal(true);
        dialog.key(com.badlogic.gdx.Input.Keys.ENTER, true); // Makes pressing Enter select "Guess"
        dialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false); // Makes pressing Escape select "Cancel"

        dialog.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (actor instanceof TextButton) {
                    TextButton button = (TextButton) actor;
                    if (button.getText().equals("Guess")) {
                        String guessedLocation = locationField.getText().trim();
                        if (!guessedLocation.isEmpty()) {
                            SendMessageService.getInstance().spyVote(username, guessedLocation, lobbyCode);
                        }
                    }
                    dialog.hide();
                }
            }
        });

        dialog.show(stage);
    }

    public void updatePlayerList(List<String> players) {
        System.out.println("Updating player list: " + players);
        Gdx.app.postRunnable(() -> {
            playersTable.clear();
            Label playersLabel = new Label("Players:", skin);
            playersTable.add(playersLabel).row();
            
            for (String player : players) {
                Label playerLabel = new Label(player, skin);
                playersTable.add(playerLabel).row();
            }
        });
    }

    public void updateScoreboard(Map<String, Integer> scores) {
        System.out.println("Updating scoreboard: " + scores);
        Gdx.app.postRunnable(() -> {
            scoreboardTable.clear();
            Label scoreLabel = new Label("Scoreboard:", skin);
            scoreboardTable.add(scoreLabel).row();
            
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                Label scoreEntry = new Label(entry.getKey() + ": " + entry.getValue(), skin);
                scoreboardTable.add(scoreEntry).row();
            }
        });
    }

    public void updateTimer(int remainingTime) {
        Gdx.app.postRunnable(() -> {
            timerLabel.setText("Time: " + remainingTime + "s");
        });
    }

    @Override
    public void update() {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        stage.dispose();
    }
} 