package io.github.Spyfall.stages.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class JoinGameDialog extends Dialog {

    public JoinGameDialog(String title, Skin skin) {
        super(title, skin);
    }

    public JoinGameDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
    }

    public JoinGameDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
    }

    {
        text("Do you want to join");
        button("Yes");
        button("No");
    }

    @Override
    public void result(Object object){

    }
}
