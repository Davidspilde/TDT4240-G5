
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

    // Layout constants
    private final float DIALOG_WIDTH = 400f;
    private final float FIELD_HEIGHT = 60f;
    private final float FIELD_FONT_SCALE = 1.3f;
    private final float TITLE_PAD_BOTTOM = 20f;
    private final float FIELD_PAD_BOTTOM = 25f;
    private final float BUTTON_HEIGHT = 55f;

    private final TextField usernameField;
    private final MainMenuController mainMenuController;
    private final AudioService audioService;

    public CreateLobbyDialog(Skin skin, AudioService audioService, MainMenuController mainMenuController, Stage stage) {
        super("", skin, "dialog");
        this.mainMenuController = mainMenuController;
        this.audioService = audioService;

        // Semi-transparent dim background
        Drawable dim = skin.newDrawable("white", new Color(0, 0, 0, 0.75f));
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        // Username field
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");
        usernameField.getStyle().font.getData().setScale(FIELD_FONT_SCALE);
        usernameField.setHeight(FIELD_HEIGHT);

        // Layout
        Table content = getContentTable();
        content.pad(30).center();
        content.add(new Label("Create a new lobby", skin)).padBottom(TITLE_PAD_BOTTOM).center().row();
        content.add(usernameField).width(DIALOG_WIDTH).height(FIELD_HEIGHT).padBottom(FIELD_PAD_BOTTOM).row();

        // Buttons
        TextButton createBtn = new TextButton("Create", skin);
        createBtn.getLabel().setFontScale(FIELD_FONT_SCALE);
        createBtn.setHeight(BUTTON_HEIGHT);

        TextButton cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.getLabel().setFontScale(FIELD_FONT_SCALE);
        cancelBtn.setHeight(BUTTON_HEIGHT);

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
