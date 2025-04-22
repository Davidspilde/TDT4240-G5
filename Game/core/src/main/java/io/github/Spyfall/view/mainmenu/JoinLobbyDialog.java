
package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class JoinLobbyDialog extends Dialog {

    private final TextField usernameField;
    private final TextField lobbyField;
    private final MainMenuController mainMenuController;
    private final AudioService audioService;

    public JoinLobbyDialog(Skin skin, AudioService audioService, MainMenuController mainMenuController, Stage stage) {
        super("", skin, "dialog");

        this.mainMenuController = mainMenuController;
        this.audioService = audioService;

        // Transparent background
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        float dialogWidth = 400f;
        float fieldHeight = 60f;

        // Create text fields
        lobbyField = new TextField("", skin);
        lobbyField.setMessageText("Enter Lobby Code");
        lobbyField.getStyle().font.getData().setScale(1.3f);
        lobbyField.setHeight(fieldHeight);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");
        usernameField.getStyle().font.getData().setScale(1.3f);
        usernameField.setHeight(fieldHeight);

        // Content
        Table content = getContentTable();
        content.pad(30).center();

        content.add(new Label("Join Lobby", skin)).padBottom(20).center().row();
        content.add(lobbyField).width(dialogWidth).height(fieldHeight).padBottom(15).row();
        content.add(usernameField).width(dialogWidth).height(fieldHeight).padBottom(25).row();

        // Buttons
        TextButton joinButton = new TextButton("Join", skin);
        joinButton.getLabel().setFontScale(1.3f);
        joinButton.setHeight(55);

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.getLabel().setFontScale(1.3f);
        cancelButton.setHeight(55);

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
