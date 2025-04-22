package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.ui.SettingsIcon;

public class MainMenuStage extends StageView {
    private final MainMenuController controller;
    private final AudioService audioService;
    private final Skin skin;

    public MainMenuStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = MainMenuController.getInstance();
        this.audioService = AudioService.getInstance();

        // Load skin
        skin = new Skin(Gdx.files.internal(
                "Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Add main menu table (custom class)
        MainMenuTable menuTable = new MainMenuTable(skin, audioService, controller, stage);
        stage.addActor(menuTable);

        // Add settings icon
        SettingsIcon settingsIcon = new SettingsIcon(skin, audioService, stage);
        stage.addActor(settingsIcon);

        // Force layout update for desktop
        Gdx.app.postRunnable(() -> {
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            menuTable.invalidateHierarchy();
            settingsIcon.invalidateHierarchy();
        });

        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }
}
