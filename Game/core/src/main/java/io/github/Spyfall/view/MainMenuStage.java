
package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class MainMenuStage extends StageView {
    private final MainMenuController controller;
    private final AudioService audioService;
    private final Skin skin;
    private final Table menuTable;
    private final Table settingsTable;

    public MainMenuStage(ScreenViewport viewport, MainMenuController controller) {
        super(viewport);
        this.controller = controller;
        this.audioService = AudioService.getInstance();

        // Load skin
        skin = new Skin(Gdx.files.internal(
                "Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Build main menu table
        menuTable = new Table(skin);
        menuTable.setFillParent(true);
        menuTable.top();
        menuTable.setBackground(new TextureRegionDrawable(
                new TextureRegion(new Texture("Background_city.png"))));
        stage.addActor(menuTable);

        // Build settings icon table
        settingsTable = new Table(skin);
        settingsTable.setFillParent(true);
        settingsTable.bottom().right();
        stage.addActor(settingsTable);

        // Initial layout
        layoutMenu();
        layoutSettings();
        menuTable.invalidateHierarchy();
        settingsTable.invalidateHierarchy();

        // Desktop workaround: force layout right before first frame
        Gdx.app.postRunnable(() -> {
            viewport.update(
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    true);
            menuTable.invalidateHierarchy();
            settingsTable.invalidateHierarchy();
        });

        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }

    private void layoutMenu() {
        menuTable.clear();

        float LOGO_H = 0.25f; // 25% of table height
        float BTN_W = 0.60f; // 60% of table width
        float BTN_H = 0.08f; // 8% of table height
        float V_GAP = 0.03f; // 3% of table height gap

        // Logo
        Image logo = new Image(new Texture("logo-Photoroom.png"));
        menuTable.add(logo)
                .prefWidth(Value.percentWidth(BTN_W, menuTable))
                .prefHeight(Value.percentHeight(LOGO_H, menuTable))
                .padBottom(Value.percentHeight(V_GAP, menuTable))
                .row();

        // Create Game Button
        TextButton createBtn = new TextButton("Create game", skin);
        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                controller.onCreateGame();
            }
        });
        menuTable.add(createBtn)
                .prefWidth(Value.percentWidth(BTN_W, menuTable))
                .prefHeight(Value.percentHeight(BTN_H, menuTable))
                .padBottom(Value.percentHeight(V_GAP, menuTable))
                .row();

        // Join Game Button
        TextButton joinBtn = new TextButton("Join game", skin);
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                showJoinGameDialog();
            }
        });
        menuTable.add(joinBtn)
                .prefWidth(Value.percentWidth(BTN_W, menuTable))
                .prefHeight(Value.percentHeight(BTN_H, menuTable))
                .padBottom(Value.percentHeight(V_GAP, menuTable))
                .row();

        // How to Play Button
        TextButton howBtn = new TextButton("How to play", skin);
        howBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                controller.onHowToPlay();
            }
        });
        menuTable.add(howBtn)
                .prefWidth(Value.percentWidth(BTN_W, menuTable))
                .prefHeight(Value.percentHeight(BTN_H, menuTable))
                .row();
    }

    private void layoutSettings() {
        settingsTable.clear();

        float ICON_SZ = 0.08f; // 8% of table width
        float PAD_SZ = 0.02f; // 2% padding

        Image settingsIcon = new Image(new Texture("settings-logo.png"));
        settingsIcon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                showSettingsDialog();
            }
        });

        settingsTable.add(settingsIcon)
                .prefWidth(Value.percentWidth(ICON_SZ, settingsTable))
                .prefHeight(Value.percentWidth(ICON_SZ, settingsTable))
                .pad(Value.percentWidth(PAD_SZ, settingsTable));
    }

    private void showJoinGameDialog() {
        float W = viewport.getWorldWidth();
        float H = viewport.getWorldHeight();

        TextField lobbyField = new TextField("", skin);
        lobbyField.setMessageText("Enter Lobby Code");
        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");

        Dialog dialog = new Dialog("Join Lobby", skin, "dialog") {
            @Override
            public void result(Object obj) {
                audioService.playSound("click");
                if (Boolean.TRUE.equals(obj)) {
                    controller.onJoinLobby(
                            usernameField.getText(),
                            lobbyField.getText());
                }
            }
        };

        dialog.getTitleTable()
                .padTop(H * 0.02f)
                .padBottom(H * 0.01f);

        Table ct = dialog.getContentTable();
        ct.add(new Label("Join an existing game", skin))
                .padBottom(H * 0.02f)
                .row();
        ct.add(lobbyField)
                .width(W * 0.6f)
                .padBottom(H * 0.02f)
                .row();
        ct.add(usernameField)
                .width(W * 0.6f)
                .row();

        dialog.button("Join", true)
                .button("Cancel", false)
                .key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
        dialog.pack();
    }

    private void showSettingsDialog() {
        float W = viewport.getWorldWidth();
        float H = viewport.getWorldHeight();

        Dialog dialog = new Dialog("Settings", skin, "dialog") {
            @Override
            public void result(Object obj) {
                audioService.playSound("click");
                audioService.saveSettings();
            }
        };

        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(audioService.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                audioService.setMusicVolume(musicSlider.getValue());
            }
        });

        Slider soundSlider = new Slider(0, 1, 0.05f, false, skin);
        soundSlider.setValue(audioService.getSoundVolume());
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                audioService.setSoundVolume(soundSlider.getValue());
            }
        });

        Table ct = dialog.getContentTable()
                .pad(H * 0.02f);
        ct.add(new Label("Music Volume", skin)).left().row();
        ct.add(musicSlider)
                .width(W * 0.6f)
                .padBottom(H * 0.02f)
                .row();
        ct.add(new Label("Sound Volume", skin)).left().row();
        ct.add(soundSlider)
                .width(W * 0.6f)
                .row();

        dialog.button("Close", true)
                .key(Input.Keys.ESCAPE, false);

        dialog.show(stage);
        dialog.pack();
    }
}
