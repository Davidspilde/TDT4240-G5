
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
    private final Skin skin;

    // Layout and timing constants
    private final float DEFAULT_DISPLAY_SECONDS = 2.0f; // How long the popup stays on screen
    private final float FADE_DURATION = 0.5f; // Fade out time
    private final float DIALOG_MAX_WIDTH = 400f; // Max width of the dialog
    private final float DIALOG_WIDTH_PERCENT = 0.8f; // Percent of screen width used for small screens
    private final float MIN_HEIGHT = 80f;
    private final float MESSAGE_PADDING = 10f;
    private final float CONTAINER_MARGIN = 40f;

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
     * Show an error popup dialog.
     *
     * @param title    Title shown at top of popup.
     * @param message  Message shown in popup.
     * @param autoHide Whether the popup disappears automatically.
     */
    public void show(String title, String message, boolean autoHide) {
        StageView currentStage = StageManager.getInstance().getStage();
        if (currentStage == null)
            return;

        Dialog dialog = new Dialog(title, skin) {
            @Override
            protected void result(Object object) {
                // Do nothing on result — placeholder override
            }
        };

        float dialogWidth = Math.min(DIALOG_MAX_WIDTH, Gdx.graphics.getWidth() * DIALOG_WIDTH_PERCENT);

        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        Container<Label> container = new Container<>(messageLabel);
        container.width(dialogWidth - CONTAINER_MARGIN);
        container.minHeight(MIN_HEIGHT);
        container.pad(MESSAGE_PADDING);

        dialog.getContentTable().add(container).width(dialogWidth - CONTAINER_MARGIN);

        if (!autoHide) {
            dialog.button("OK", true);
        }

        dialog.setMovable(false);
        dialog.setResizable(false);

        dialog.setPosition(
                (Gdx.graphics.getWidth() - dialogWidth) / 2f,
                Gdx.graphics.getHeight() / 2f);

        dialog.show(currentStage.getStage());

        if (autoHide) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    dialog.addAction(Actions.sequence(
                            Actions.fadeOut(FADE_DURATION),
                            Actions.removeActor()));
                }
            }, DEFAULT_DISPLAY_SECONDS);
        }
    }
}
