package io.github.Spyfall.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.view.StageView;

public class ErrorPopup {
    private static ErrorPopup instance;
    private Skin skin;
    private static final float DEFAULT_DISPLAY_SECONDS = 2.0f; // popup duration
    private static final float FADE_DURATION = 0.5f; // fade duration
    
    private ErrorPopup() {
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));
    }
    
    public static ErrorPopup getInstance() {
        if (instance == null) {
            instance = new ErrorPopup();
        }
        return instance;
    }
    
    public void showServerError(String errorEvent, String errorMessage) {
        show("Server Error: " + errorEvent, errorMessage, true);
    }
    
    public void showClientError(String errorMessage) {
        show("Error", errorMessage, true);
    }
    
    /**
     * Show an error popup
     * @param title The title of the error
     * @param message The error message
     * @param autoHide If true, popup will automatically disappear after a few seconds
     */
    public void show(String title, String message, boolean autoHide) {
        StageView currentStage = StageManager.getInstance().getStage();
        if (currentStage == null) return;
        
        Dialog dialog = new Dialog(title, skin) {
            @Override
            protected void result(Object object) {
                // dialog closed
            }
        };
        
        // max width
        float dialogWidth = Math.min(400, Gdx.graphics.getWidth() * 0.8f);
        
        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true); // Enable text wrapping
        messageLabel.setAlignment(Align.center);
        

        Container<Label> container = new Container<>(messageLabel);
        container.width(dialogWidth - 40);
        container.minHeight(80);
        container.pad(10);
        
        dialog.getContentTable().add(container).width(dialogWidth - 40);
        
        // add button if not auto-hiding
        if (!autoHide) {
            dialog.button("OK", true);
        }
        
        dialog.setMovable(false);
        dialog.setResizable(false);
        

        dialog.setPosition(
            (Gdx.graphics.getWidth() - dialogWidth) / 2,
            Gdx.graphics.getHeight() / 2
        );
        
        dialog.show(currentStage.getStage());
        

        if (autoHide) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.addAction(Actions.sequence(
                        Actions.fadeOut(FADE_DURATION),
                        Actions.removeActor()
                    ));
                }
                }, DEFAULT_DISPLAY_SECONDS);
        }
    }
}