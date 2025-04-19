package io.github.Spyfall.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.view.StageView;

public class ErrorPopup {
    private static ErrorPopup instance;
    private Skin skin;
    private static final float DEFAULT_DISPLAY_SECONDS = 2.0f; // How long to show the popup
    private static final float FADE_DURATION = 0.5f; // How long the fade animation takes
    
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
                // Dialog dismissed
            }
        };
        
        // Configure the dialog
        dialog.text(message);
        
        // Only add button if not auto-hiding
        if (!autoHide) {
            dialog.button("OK", true);
        }
        
        dialog.setMovable(false);
        dialog.setResizable(false);
        
        // Adapt size to screen dimensions
        float width = Math.min(400, Gdx.graphics.getWidth() * 0.8f);
        dialog.setWidth(width);
        dialog.setPosition(
            (Gdx.graphics.getWidth() - width) / 2,
            Gdx.graphics.getHeight() / 2
        );
        
        // Show the dialog
        dialog.show(currentStage.getStage());
        
        // Auto-hide with fade effect if requested
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
    
    /**
     * Show an error popup with default settings (auto-hide)
     */
    public void show(String title, String message) {
        show(title, message, true);
    }
}