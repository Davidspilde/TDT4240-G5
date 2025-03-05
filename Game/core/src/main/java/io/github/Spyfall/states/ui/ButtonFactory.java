package io.github.Spyfall.states.ui;

import com.badlogic.gdx.graphics.Texture;

import io.github.Spyfall.ecs.Entity;
import io.github.Spyfall.ecs.components.ButtonComponent;
import io.github.Spyfall.ecs.components.ButtonComponent.ButtonEnum;
import io.github.Spyfall.ecs.observers.ButtonListener;

public class ButtonFactory {

    public static Entity createButton(ButtonEnum type, ButtonListener listener){
        Entity button = new Entity();
        Runnable callback = () -> {
            listener.onAction(type);
        };

        ButtonComponent buttonComponent = new ButtonComponent(type, callback);

        return button;
    }

//    private static Texture getTexture(ButtonEnum type){
//        switch (type){
//
//        }
//    }
}
