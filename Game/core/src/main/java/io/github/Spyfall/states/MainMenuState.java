package io.github.Spyfall.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.ecs.ECSManager;
import io.github.Spyfall.ecs.Entity;
import io.github.Spyfall.ecs.components.DrawableComponent;
import io.github.Spyfall.ecs.components.PositionComponent;
import io.github.Spyfall.ecs.observers.ButtonListener;
import io.github.Spyfall.ecs.components.ButtonComponent;
import io.github.Spyfall.ecs.systems.RenderingSystem;
import io.github.Spyfall.states.ui.ButtonFactory;

public class MainMenuState extends State implements ButtonListener {

    private boolean buttonPressed;

    public MainMenuState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void initState(ECSManager ecsManager, OrthographicCamera camera, Viewport viewport) {
        //Set systems used by this state
        ecsManager.addSystem(new RenderingSystem(camera,viewport));

        Entity background = new Entity();
        PositionComponent positionComponent = new PositionComponent(0,0);
        DrawableComponent drawableComponent = new DrawableComponent(DrawableComponent.DrawableType.SPRITE,new TextureRegion(new Texture("Background_city.png")),null,new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()),-1,1);

        background.addComponent(positionComponent);
        background.addComponent(drawableComponent);
        ecsManager.addEntity(background);

        //logo
        Entity logo = new Entity();
        PositionComponent positionComponent1 = new PositionComponent(148,950);
        TextureRegion region = new TextureRegion(new Texture("logo-Photoroom.png"));
        DrawableComponent drawableComponent1 = new DrawableComponent(DrawableComponent.DrawableType.SPRITE,region,null,new Vector2(region.getRegionWidth(),region.getRegionHeight()),0,1);

        logo.addComponent(positionComponent1);
        logo.addComponent(drawableComponent1);
        ecsManager.addEntity(logo);


        //720-440=280
        Entity create_game_button = ButtonFactory.createButton(ButtonComponent.ButtonEnum.CREATE_LOBBY,this,new Vector2(140,550),null,1);
        ecsManager.addEntity(create_game_button);

        Entity join_game_button = ButtonFactory.createButton(ButtonComponent.ButtonEnum.JOIN_LOBBY,this,new Vector2(140,300),null,1);
        ecsManager.addEntity(join_game_button);
    }

    @Override
    public void onAction(ButtonComponent.ButtonEnum type) {
        switch (type){

        }
    }
}
