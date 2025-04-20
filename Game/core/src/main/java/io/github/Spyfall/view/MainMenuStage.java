package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class MainMenuStage extends StageView {
    private MainMenuController controller;
    private Skin skin;
    private final AudioService audioService;

    public MainMenuStage(ScreenViewport viewport, MainMenuController controller) {
        super(viewport);
        this.controller = controller;
        audioService = AudioService.getInstance();
        initMainMenu();
    }

    private void initMainMenu() {

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(
                Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        TextButton createGameButton = new TextButton("Create game", skin);
        TextButton joinGameButton = new TextButton("Join game", skin);
        TextButton howToPlayButton = new TextButton("How to play", skin);

        //Settings
        TextureRegion region = new TextureRegion(new Texture("settings-logo.png"));
        Image settings = new Image(region);

        settings.setSize((float) (viewport.getScreenWidth()*0.1), (float) (viewport.getScreenHeight()*0.1));
        TextureRegionDrawable texture = new TextureRegionDrawable(
                new TextureRegion(new Texture("Background_city.png")));
        Table table = new Table();
        Image image = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks to buttons
        createGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                controller.onCreateGame();
            }
        });

        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                showJoinGameDialog();
            }
        });

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onHowToPlay();
            }
        });

        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                showSettingsDialog();
            }
        });

        // Align Table to Top
        table.top().setFillParent(true);
        table.setBackground(texture);

        // Add padding and spacing
        table.add(image).padBottom((float) viewport.getScreenHeight() / 10)
                .padLeft((float) viewport.getScreenWidth() / 10).padRight((float) viewport.getScreenWidth() / 10)
                .padTop((float) viewport.getScreenHeight() / 15);
        table.row();
        table.add(createGameButton).padBottom((float) viewport.getScreenHeight() / 10);
        table.row();
        table.add(joinGameButton).padBottom((float) viewport.getScreenHeight() / 10);
        table.row();
        table.add(howToPlayButton).padBottom((float) viewport.getScreenHeight() / 10);


        Table bottomRightTable = new Table();
        bottomRightTable.setFillParent(true);
        bottomRightTable.bottom().right();
        bottomRightTable.add(settings).width(viewport.getScreenWidth() * 0.15f) // 50% of screen width
                .height(viewport.getScreenHeight() * 0.075f)
                .pad(20); // 20% of screen height

        // Add UI to Stage
        stage.addActor(table);
        stage.addActor(bottomRightTable);
    }

    private void showJoinGameDialog() {
        final TextField username = new TextField("", skin);
        final TextField textField = new TextField("", skin);
        textField.setMessageText("Enter Lobby Code");
        username.setMessageText("Enter Username");

        Dialog dialog = new Dialog("", skin, "dialog") {
            @Override
            public void result(Object obj) {
                audioService.playSound("click");
                if (obj.equals(true)) {  // Only change stage if "Join" is pressed
                    String lobbyCode = textField.getText();
                    String usernameString = username.getText();
                    System.out.println("User typed lobbycode: " + lobbyCode + "\n" + "Username: " + usernameString);
                    controller.onJoinLobby(usernameString, lobbyCode);
                }
            }
        };

        dialog.getTitleTable().padTop(20f);
        dialog.getTitleTable().padBottom(5f);

        Label label = new Label("Join Lobby", skin);
        label.setAlignment(Align.center);
        label.setWrap(true);

        ScrollPane scrollPane = new ScrollPane(label, skin);
        scrollPane.setFadeScrollBars(false);
        dialog.getContentTable().add(scrollPane).width((viewport.getScreenWidth()*0.8f)).height((viewport.getScreenWidth()*0.2f)).row();
        dialog.getContentTable().add(textField).width(250).center().pad(15).row();
        dialog.getContentTable().add(username).width(250).center().pad(15);

        dialog.button("Join", true); // Sends "true" when clicked
        dialog.button("Cancel", false);  // Sends "false" when clicked
        dialog.key(Input.Keys.ENTER, true); // Pressing ENTER is the same as clicking "Yes"

        dialog.show(stage);
        dialog.pack(); // for calculating layout libgdx stuff

        dialog.setSize(dialog.getWidth(), dialog.getHeight() + 50);
    }

    private void showNoLobbyDialog(String lobbyCode) {
        Dialog dialog = new Dialog("No lobby with code "+lobbyCode+ " found",skin,"dialog") {
            @Override
            public void result(Object obj){
                audioService.playSound("click");
            }
        };

        dialog.getTitleTable().padTop(20f);
        dialog.getTitleTable().padBottom(5f);

        dialog.button("Ok",true);
        dialog.key(Input.Keys.ENTER, true);
        dialog.show(stage);
        dialog.pack();
        dialog.setSize(dialog.getWidth(), dialog.getHeight() + 50);
    }

    private void showSettingsDialog() {
        Dialog dialog = new Dialog("Settings", skin, "dialog") {
            @Override
            public void result(Object obj) {
                audioService.playSound("click");
                // Save settings when dialog closes
                audioService.saveSettings();
            }
        };

        // Music Volume Slider
        final Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(audioService.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                audioService.setMusicVolume(musicSlider.getValue());
            }
        });

        dialog.getTitleTable().padTop(20f);
        dialog.getTitleTable().padBottom(5f);
        // Sound Volume Slider
        final Slider soundSlider = new Slider(0, 1, 0.05f, false, skin);
        soundSlider.setValue(audioService.getSoundVolume());
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audioService.setSoundVolume(soundSlider.getValue());
            }
        });

        // Add components to dialog
        Table contentTable = dialog.getContentTable();
        contentTable.pad(20);

        contentTable.add(new Label("Music Volume:", skin)).left().padRight(10).row();
        contentTable.add(musicSlider).width(200).center().pad(15).row();
        contentTable.add(new Label("Sound Volume:", skin)).left().padRight(10).row();
        contentTable.add(soundSlider).width(200).center().pad(15).row();

        dialog.button("Close", true);
        dialog.key(Input.Keys.ESCAPE, false); // Close with ESC
        // Center and show dialog
        dialog.show(stage);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
    }
}
