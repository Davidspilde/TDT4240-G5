package io.github.Spyfall.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

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
    private boolean transformation;

    public RenderingSystem(OrthographicCamera camera, Viewport viewport){
        this.sb = new SpriteBatch();
        this.camera = camera;
        this.viewport = viewport;
        this.transformation = false;
    }

    @Override
    public void update(List<Entity> entities) {
        clearScreen();
        sb.setProjectionMatrix(camera.combined);
        Collections.sort(entities, (e1, e2) -> {
            DrawableComponent d1 = (DrawableComponent) e1.getComponent(DrawableComponent.class);
            DrawableComponent d2 = (DrawableComponent) e2.getComponent(DrawableComponent.class);
            // Handle null values for DrawableComponent
            if (d1 != null && d2 != null) {
                return Integer.compare(d1.getZ_index(), d2.getZ_index());
            } else if (d1 != null) {
                return -1;  // e1 has DrawableComponent, so it comes first
            } else if (d2 != null) {
                return 1;   // e2 has DrawableComponent, so it comes first
            }
            return 0; // Both don't have DrawableComponent, so no change
        });
        sb.begin();
        for (Entity entity : entities) {
            if(entity.hasComponent(TransformComponent.class)){
                transformation = true;
            }
            if(entity.hasComponent(DrawableComponent.class)&&entity.hasComponent(PositionComponent.class)){
                DrawableComponent drawableComponent = (DrawableComponent) entity.getComponent(DrawableComponent.class);
                PositionComponent positionComponent = (PositionComponent) entity.getComponent(PositionComponent.class);
                if(transformation){
                    TransformComponent transformComponent = (TransformComponent) entity.getComponent(TransformComponent.class);
                    transform(positionComponent,transformComponent);
                }
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
            case SPRITE -> {
                drawableComponent.getSprite().setPosition(pos.x, pos.y);
                drawableComponent.getSprite().draw(sb);
            }
            case ANIMATION -> {
                drawableComponent.getSprite().setPosition(pos.x, pos.y);
                drawableComponent.getSprite().draw(sb);
                drawableComponent.changeFrame();
                drawableComponent.incStateTime();
            }
        }
    }
}
