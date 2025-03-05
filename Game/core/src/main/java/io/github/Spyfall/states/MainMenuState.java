package io.github.Spyfall.states;

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
        DrawableComponent drawableComponent = new DrawableComponent(DrawableComponent.DrawableType.SPRITE,new TextureRegion(new Texture("Background.jpg")),null,new Vector2(1,1),-1);

        background.addComponent(positionComponent);
        background.addComponent(drawableComponent);
        ecsManager.addEntity(background);

        Entity button = ButtonFactory.createButton(ButtonComponent.ButtonEnum.CREATE_LOBBY,this,new Vector2(0.35F,0.35F),null);
        ecsManager.addEntity(button);
    }

    @Override
    public void onAction(ButtonComponent.ButtonEnum type) {
        switch (type){
            case JOIN_LOBBY -> stateManager.setState(States.JOIN_LOBBY);
            case CREATE_LOBBY -> stateManager.setState(States.CREATE_LOBBY);
//            case TUTORIAL -> stateManager.setState(new );
        }
    }
}
