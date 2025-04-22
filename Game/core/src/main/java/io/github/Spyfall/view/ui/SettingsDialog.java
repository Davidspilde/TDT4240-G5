
package io.github.Spyfall.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import io.github.Spyfall.services.AudioService;

public class SettingsDialog extends Dialog {
    private final AudioService audioService;

    public SettingsDialog(Skin skin, AudioService audioService, Stage stage) {
        super("", skin, "dialog");
        this.audioService = audioService;

        // Dimmed transparent background behind dialog
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        float dialogWidth = 400f;

        // Music volume slider
        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(audioService.getMusicVolume());
        musicSlider.setHeight(40);
        musicSlider.addListener(event -> {
            audioService.setMusicVolume(musicSlider.getValue());
            return false;
        });

        // Sound volume slider
        Slider soundSlider = new Slider(0, 1, 0.05f, false, skin);
        soundSlider.setValue(audioService.getSoundVolume());
        soundSlider.setHeight(40);
        soundSlider.addListener(event -> {
            audioService.setSoundVolume(soundSlider.getValue());
            return false;
        });

        // Content layout
        Table content = getContentTable();
        content.pad(40).center();

        Label title = new Label("Settings", skin);
        title.setFontScale(1.5f);
        title.setAlignment(Align.center);
        content.add(title).padBottom(30).colspan(2).center().row();

        // Music
        Label musicLabel = new Label("Music Volume", skin);
        musicLabel.setFontScale(1.2f);
        content.add(musicLabel).left().padBottom(10).colspan(2).row();
        content.add(musicSlider).width(dialogWidth).padBottom(30).colspan(2).row();

        // Sound
        Label soundLabel = new Label("Sound Volume", skin);
        soundLabel.setFontScale(1.2f);
        content.add(soundLabel).left().padBottom(10).colspan(2).row();
        content.add(soundSlider).width(dialogWidth).padBottom(30).colspan(2).row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabel().setFontScale(1.3f);
        closeButton.setHeight(50);
        button(closeButton, true);

        key(Input.Keys.ESCAPE, false);

        show(stage);
        pack();
    }

    @Override
    protected void result(Object obj) {
        // Save audio settings when dialog is closed
        audioService.saveSettings();
        audioService.playSound("click");
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}
