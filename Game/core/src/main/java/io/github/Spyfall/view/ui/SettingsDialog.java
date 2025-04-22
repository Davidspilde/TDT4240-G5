package io.github.Spyfall.view.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.Spyfall.services.AudioService;

public class SettingsDialog extends Dialog {
    private AudioService audioService;

    public SettingsDialog(Skin skin, AudioService audioService, Stage stage) {
        super("", skin, "dialog");

        this.audioService = audioService;
        // Makes backgorund transparent when used
        Drawable dim = skin.newDrawable("white", UIConstants.transparentBlack);
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());

        getStyle().stageBackground = dim;

        float W = stage.getViewport().getWorldWidth();
        float H = stage.getViewport().getWorldHeight();

        // Music Volume Slider
        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(audioService.getMusicVolume());
        musicSlider.addListener(event -> {
            audioService.setMusicVolume(musicSlider.getValue());
            return false;
        });

        // Sound Volume Slider
        Slider soundSlider = new Slider(0, 1, 0.05f, false, skin);
        soundSlider.setValue(audioService.getSoundVolume());
        soundSlider.addListener(event -> {
            audioService.setSoundVolume(soundSlider.getValue());
            return false;
        });

        Table content = getContentTable().pad(H * UIConstants.DIALOG_PADDING);

        content.add(new Label("Settings", skin)).padBottom(H * UIConstants.DIALOG_PADDING).row();
        content.add(new Label("Music Volume", skin)).left().row();
        content.add(musicSlider).width(W * UIConstants.DIALOG_WIDTH_PERCENT).padBottom(H * UIConstants.DIALOG_PADDING)
                .row();
        content.add(new Label("Sound Volume", skin)).left().row();
        content.add(soundSlider).width(W * UIConstants.DIALOG_WIDTH_PERCENT).row();

        button("Close", true);
        key(Input.Keys.ESCAPE, false);

        show(stage);
        pack();
    }

    @Override
    protected void result(Object obj) {
        audioService.saveSettings(); // optional
        audioService.playSound("click");
    }
}
