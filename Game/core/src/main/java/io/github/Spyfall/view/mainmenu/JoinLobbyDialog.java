
package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class JoinLobbyDialog extends Dialog {

    // Layout constants
    private final float DIALOG_WIDTH = 400f;
    private final float FIELD_HEIGHT = 60f;
    private final float FONT_SCALE = 1.3f;
    private final float FIELD_PAD = 15f;
    private final float TITLE_PAD_BOTTOM = 20f;
    private final float FINAL_FIELD_PAD_BOTTOM = 25f;
    private final float BUTTON_HEIGHT = 55f;

    private final TextField usernameField;
    private final TextField lobbyField;
    private final MainMenuController mainMenuController;
    private final AudioService audioService;

    public JoinLobbyDialog(Skin skin, AudioService audioService, MainMenuController mainMenuController, Stage stage) {
        super("", skin, "dialog");

        this.mainMenuController = mainMenuController;
        this.audioService = audioService;

        // Dimmed transparent background
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        // Lobby code field
        lobbyField = new TextField("", skin);
        lobbyField.setMessageText("Enter Lobby Code");
        lobbyField.getStyle().font.getData().setScale(FONT_SCALE);
        lobbyField.setHeight(FIELD_HEIGHT);

        // Username field
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");
        usernameField.getStyle().font.getData().setScale(FONT_SCALE);
        usernameField.setHeight(FIELD_HEIGHT);

        // Content layout
        Table content = getContentTable();
        content.pad(30).center();
        content.add(new Label("Join Lobby", skin)).padBottom(TITLE_PAD_BOTTOM).center().row();
        content.add(lobbyField).width(DIALOG_WIDTH).height(FIELD_HEIGHT).padBottom(FIELD_PAD).row();
        content.add(usernameField).width(DIALOG_WIDTH).height(FIELD_HEIGHT).padBottom(FINAL_FIELD_PAD_BOTTOM).row();

        // Buttons
        TextButton joinButton = new TextButton("Join", skin);
        joinButton.getLabel().setFontScale(FONT_SCALE);
        joinButton.setHeight(BUTTON_HEIGHT);

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.getLabel().setFontScale(FONT_SCALE);
        cancelButton.setHeight(BUTTON_HEIGHT);

        button(joinButton, true);
        button(cancelButton, false);

        // Keyboard shortcuts
        key(Input.Keys.ENTER, true);
        key(Input.Keys.ESCAPE, false);

        show(stage);
        pack();
    }

    @Override
    protected void result(Object obj) {
        audioService.playSound("click");
        if (Boolean.TRUE.equals(obj)) {
            mainMenuController.onJoinLobby(usernameField.getText().trim(), lobbyField.getText().trim());
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}
