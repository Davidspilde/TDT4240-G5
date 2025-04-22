
package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class CreateLobbyDialog extends Dialog {
    private final TextField usernameField;
    private final MainMenuController mainMenuController;
    private final AudioService audioService;

    public CreateLobbyDialog(Skin skin, AudioService audioService, MainMenuController mainMenuController, Stage stage) {
        super("", skin, "dialog");
        this.mainMenuController = mainMenuController;
        this.audioService = audioService;

        // Semi-transparent background
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        float dialogWidth = 400f;
        float fieldHeight = 60f;

        // Create field
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");
        usernameField.getStyle().font.getData().setScale(1.3f);
        usernameField.setHeight(fieldHeight);

        // Content table
        Table ct = getContentTable();
        ct.pad(30).center();

        ct.add(new Label("Create a new lobby", skin)).padBottom(20).center().row();
        ct.add(usernameField).width(dialogWidth).height(fieldHeight).padBottom(25).row();

        // Buttons
        TextButton createBtn = new TextButton("Create", skin);
        createBtn.getLabel().setFontScale(1.3f);
        createBtn.setHeight(55);

        TextButton cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.getLabel().setFontScale(1.3f);
        cancelBtn.setHeight(55);

        button(createBtn, true);
        button(cancelBtn, false);

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
            mainMenuController.onCreateLobby(usernameField.getText().trim());
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}
