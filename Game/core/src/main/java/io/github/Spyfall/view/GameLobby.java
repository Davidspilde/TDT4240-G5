package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameLobby extends StageView {

    private Skin skin;
    private ScreenViewport viewport;
    private Stage stage;

    // UI elements
    private Label timerLabel;
    private Label locationLabel;
    private Label roleLabel;
    private TextButton endGameButton;
    private TextButton leaveGameButton;

    // The background texture
    private Texture bgTexture;

    private boolean isSpy;
    private String locationName;
    private String roleName;

    public GameLobby(boolean isSpy, String locationName, String roleName,ScreenViewport viewport) {
        super(viewport);
        this.isSpy = isSpy;
        this.locationName = locationName;
        this.roleName = roleName;
        stage = new Stage(viewport);
        initStage();
    }

    public void initStage() {
        // 1) Load the background texture
        bgTexture = new Texture(Gdx.files.internal("bg.jpeg"));

        // Let the stage receive input events
        Gdx.input.setInputProcessor(stage);

        // Load skin
        skin = new Skin(Gdx.files.internal("metal-ui.json"));

        // 2) Create the root table that fills the screen
        Table rootTable = new Table();

        // 3) Create a drawable from the background texture and set as table background
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);

        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Create timer label for top of screen
        timerLabel = new Label("2:53", skin);
        timerLabel.setAlignment(Align.center);

        // Create location label and role label
        // If player is spy, location name is replaced with ???
        String displayedLocation = isSpy ? "???" : locationName;
        locationLabel = new Label(displayedLocation, skin);
        roleLabel = new Label(roleName, skin);

        // A container (vertical group) for the top “info” area
        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.addActor(timerLabel);
        infoGroup.addActor(locationLabel);
        infoGroup.addActor(roleLabel);

        // Create the list of players
        Table playersTable = new Table(skin);
        Label player1 = new Label("Player 1", skin);
        Label player2 = new Label("Player 2", skin);
        playersTable.add(player1).row();
        playersTable.add(player2).row();

        // Create the list of possible locations
        Table possibleLocationsTable = new Table(skin);
        Label loc1 = new Label("Airplane", skin);
        Label loc2 = new Label("Bank", skin);
        Label loc3 = new Label("Beach", skin);
        possibleLocationsTable.add(loc1).row();
        possibleLocationsTable.add(loc2).row();
        possibleLocationsTable.add(loc3).row();

        // Create end/leave game buttons
        endGameButton = new TextButton("End Game", skin);
        leaveGameButton = new TextButton("Leave Game", skin);

        // Layout with rootTable
        rootTable.top().pad(20f);

        // Top row: infoGroup
        rootTable.add(infoGroup).expandX().center().colspan(2).row();

        // Middle area: players (left), locations (right)
        rootTable.row().padTop(30);
        rootTable.add(playersTable).expand().fill().padRight(20);
        rootTable.add(possibleLocationsTable).expand().fill();

        // Bottom area: End game / Leave game side by side
        Table bottomButtonsTable = new Table();
        bottomButtonsTable.add(endGameButton).padRight(20);
        bottomButtonsTable.add(leaveGameButton);

        rootTable.row().padTop(30);
        rootTable.add(bottomButtonsTable).colspan(2);

        // Optionally set debug to see table outlines
//        rootTable.setDebug(true);
    }

    public void update() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage(){
        return stage;
    }

    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        stage.dispose();
    }
}
