package io.github.Spyfall.states.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.Spyfall.ecs.Entity;
import io.github.Spyfall.ecs.components.ButtonComponent;
import io.github.Spyfall.ecs.components.ButtonComponent.ButtonEnum;
import io.github.Spyfall.ecs.components.DrawableComponent;
import io.github.Spyfall.ecs.components.PositionComponent;
import io.github.Spyfall.ecs.observers.ButtonListener;

public class ButtonFactory {

    public static Entity createButton(ButtonEnum type, ButtonListener listener, Vector2 position,Vector2 size,int scale){
        Entity button = new Entity();
        Runnable callback = () -> {
            listener.onAction(type);
        };

        ButtonComponent buttonComponent = new ButtonComponent(type, callback);
        PositionComponent positionComponent = new PositionComponent(position);
        TextureRegion region = getTexture(type);
        assert region != null;
        DrawableComponent drawableComponent = new DrawableComponent(DrawableComponent.DrawableType.SPRITE,region,null,new Vector2(region.getRegionWidth(),region.getRegionHeight()),0,scale);
        button.addComponent(buttonComponent);
        button.addComponent(positionComponent);
        button.addComponent(drawableComponent);
        return button;
    }

    private static TextureRegion getTexture(ButtonEnum type){
        switch (type){
            case CREATE_LOBBY -> {
                return new TextureRegion(new Texture("create_game.png"));
            }
        }
        return null;
    }
}
