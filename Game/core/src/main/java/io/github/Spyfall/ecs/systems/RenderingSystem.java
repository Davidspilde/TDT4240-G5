package io.github.Spyfall.ecs.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import io.github.Spyfall.ecs.Entity;
import io.github.Spyfall.ecs.System;
import io.github.Spyfall.ecs.components.DrawableComponent;
import io.github.Spyfall.ecs.components.PositionComponent;
import io.github.Spyfall.ecs.components.TransformComponent;

public class RenderingSystem implements System {
    private SpriteBatch sb;
    private OrthographicCamera camera;
    private Viewport viewport;
    private List<Entity> entities;

    public RenderingSystem(OrthographicCamera camera, Viewport viewport){
        this.sb = new SpriteBatch();
        this.camera = camera;
        this.viewport = viewport;
    }

    @Override
    public void update(List<Entity> entities) {
        clearScreen();
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        for (Entity entity : entities) {
            if(entity.hasComponent(DrawableComponent.class)&&entity.hasComponent(PositionComponent.class)){
                DrawableComponent drawableComponent = (DrawableComponent) entity.getComponent(DrawableComponent.class);
                PositionComponent positionComponent = (PositionComponent) entity.getComponent(PositionComponent.class);
                TransformComponent transformComponent = (TransformComponent) entity.getComponent(TransformComponent.class);
                drawEntity(drawableComponent, positionComponent.getPosition(), sb);
            }
        }
        sb.end();
    }

    private Vector2 transform(PositionComponent positionComponent, TransformComponent transformComponent) {
        // Bare koordinat transform, mulig å legge til en rotation og sånt etterhvert
        // Mulig det er bedre å lage en sprite basert på de tre komponentene
        return new Vector2(positionComponent.getXPosition()+transformComponent.getXTransform(),positionComponent.getYPosition()+transformComponent.getYTransform());
    }

    private void clearScreen(){
        ScreenUtils.clear(0, 0, 0, 1);
    }
    private void drawEntity(DrawableComponent drawableComponent, Vector2 pos, SpriteBatch sb){
        switch (drawableComponent.getType()){
            case SPRITE -> sb.draw(drawableComponent.getRegion(),pos.x,pos.y,drawableComponent.getSize().x,drawableComponent.getSize().y);
            //Reduntant med to like draw calls?
            case ANIMATION -> {
                sb.draw(drawableComponent.getRegion(),pos.x,pos.y,drawableComponent.getSize().x,drawableComponent.getSize().y);
                //muligens endre til setStateTime hvis vi vil at components kun skal ha data
                drawableComponent.incStateTime();
            }
        }
    }
}
