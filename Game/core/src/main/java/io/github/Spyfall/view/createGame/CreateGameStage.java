package io.github.Spyfall.view.createGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.controller.MainController;
import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.view.StageView;

public class CreateGameStage extends StageView {
    private LobbyController controller;
    private MainController mainController;
    private Skin skin;
    private TextField usernameField;

    public CreateGameStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = LobbyController.getInstance();
        this.mainController = MainController.getInstance();

        this.skin = new Skin(
                Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));
        initStage();
    }

    public void initStage() {
        Gdx.input.setInputProcessor(stage);

        // Create UI Elements
        Label titleLabel = new Label("Create Game", skin);
        titleLabel.setAlignment(Align.center);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter your username");

        TextButton createButton = new TextButton("Create Game", skin);
        TextButton backButton = new TextButton("Back", skin);

        // Background
        TextureRegionDrawable texture = new TextureRegionDrawable(
                new TextureRegion(new Texture("Background_city.png")));
        Table mainTable = new Table();
        Image logo = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    controller.createLobby(username);
                    // The transition to GameConfigStage will happen in the ReceiveMessageService
                    // when we get the lobbyCreated response
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainController.setMainMenuStage();
            }
        });

        // Layout
        mainTable.setFillParent(true);
        mainTable.setBackground(texture);

        // Top section with logo
        Table topTable = new Table();
        topTable.add(logo).padBottom(20).row();
        topTable.add(titleLabel).padBottom(20);

        // Form table
        Table formTable = new Table();
        formTable.add(usernameField).width(200).padBottom(10).row();
        formTable.add(createButton).padBottom(10).row();
        formTable.add(backButton);

        // Main layout
        mainTable.add(topTable).expandX().center().padTop(20).row();
        mainTable.add(formTable).expandX().center().padTop(20);

        // Add UI to Stage
        stage.addActor(mainTable);
    }
}
