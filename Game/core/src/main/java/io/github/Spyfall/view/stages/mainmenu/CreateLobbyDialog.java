package io.github.Spyfall.view.stages.mainmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.ui.UIConstants;

public class CreateLobbyDialog extends Dialog {
    private final TextField usernameField;
    private final MainMenuController mainMenuController;
    private final AudioService audioService;

    public CreateLobbyDialog(Skin skin, AudioService audioService, MainMenuController mainMenuController, Stage stage) {
        super("", skin, "dialog");
        this.mainMenuController = mainMenuController;
        this.audioService = audioService;

        // Makes backgorund transparent when used
        Drawable dim = skin.newDrawable("white", UIConstants.transparentBlack);
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());

        getStyle().stageBackground = dim;

        float W = stage.getViewport().getWorldWidth();
        float H = stage.getViewport().getWorldHeight();

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter Username");

        getTitleTable()
                .padTop(H * UIConstants.TITLE_TOP_PAD)
                .padBottom(H * UIConstants.TITLE_BOTTOM_PAD);

        Table ct = getContentTable();
        ct.add(new Label("Create a new lobby", skin)).padBottom(H * UIConstants.DIALOG_PADDING).row();
        ct.add(usernameField)
                .width(W * UIConstants.DIALOG_WIDTH_PERCENT)
                .padBottom(H * UIConstants.DIALOG_PADDING)
                .row();

        button("Create", true);
        button("Cancel", false);
        key(Input.Keys.ENTER, true);
        key(Input.Keys.ESCAPE, false);

        show(stage);
        pack();
    }

    @Override
    protected void result(Object obj) {
        audioService.playSound("click");
        if (Boolean.TRUE.equals(obj)) {
            mainMenuController.onCreateLobby(usernameField.getText());
        }
    }
}
