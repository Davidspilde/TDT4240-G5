package io.github.Spyfall.stages.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ButtonFactory {
    static public TextButton createTextButton(String text, Skin skin, Runnable callback){
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(callback!=null){
                    callback.run();
                }
            }
        });
        return button;
    }
}
