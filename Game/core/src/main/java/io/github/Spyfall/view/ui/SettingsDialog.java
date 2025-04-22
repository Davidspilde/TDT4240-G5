
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

    // Layout constants
    private final float DIALOG_WIDTH = 400f;
    private final float SLIDER_HEIGHT = 40f;
    private final float TITLE_FONT_SCALE = 1.5f;
    private final float LABEL_FONT_SCALE = 1.2f;
    private final float BUTTON_FONT_SCALE = 1.3f;
    private final float BUTTON_HEIGHT = 50f;
    private final float PADDING = 40f;
    private final float SLIDER_PAD_BOTTOM = 30f;
    private final float LABEL_PAD_BOTTOM = 10f;
    private final float TITLE_PAD_BOTTOM = 30f;

    private final AudioService audioService;

    public SettingsDialog(Skin skin, AudioService audioService, Stage stage) {
        super("", skin, "dialog");
        this.audioService = audioService;

        // Semi-transparent dim background
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        // Create sliders
        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(audioService.getMusicVolume());
        musicSlider.setHeight(SLIDER_HEIGHT);
        musicSlider.addListener(event -> {
            audioService.setMusicVolume(musicSlider.getValue());
            return false;
        });

        Slider soundSlider = new Slider(0, 1, 0.05f, false, skin);
        soundSlider.setValue(audioService.getSoundVolume());
        soundSlider.setHeight(SLIDER_HEIGHT);
        soundSlider.addListener(event -> {
            audioService.setSoundVolume(soundSlider.getValue());
            return false;
        });

        // Layout
        Table content = getContentTable();
        content.pad(PADDING).center();

        Label title = new Label("Settings", skin);
        title.setFontScale(TITLE_FONT_SCALE);
        title.setAlignment(Align.center);
        content.add(title).padBottom(TITLE_PAD_BOTTOM).colspan(2).center().row();

        Label musicLabel = new Label("Music Volume", skin);
        musicLabel.setFontScale(LABEL_FONT_SCALE);
        content.add(musicLabel).left().padBottom(LABEL_PAD_BOTTOM).colspan(2).row();
        content.add(musicSlider).width(DIALOG_WIDTH).padBottom(SLIDER_PAD_BOTTOM).colspan(2).row();

        Label soundLabel = new Label("Sound Volume", skin);
        soundLabel.setFontScale(LABEL_FONT_SCALE);
        content.add(soundLabel).left().padBottom(LABEL_PAD_BOTTOM).colspan(2).row();
        content.add(soundSlider).width(DIALOG_WIDTH).padBottom(SLIDER_PAD_BOTTOM).colspan(2).row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabel().setFontScale(BUTTON_FONT_SCALE);
        closeButton.setHeight(BUTTON_HEIGHT);
        button(closeButton, true);

        key(Input.Keys.ESCAPE, false);

        show(stage);
        pack();
    }

    @Override
    protected void result(Object obj) {
        audioService.saveSettings();
        audioService.playSound("click");
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}
