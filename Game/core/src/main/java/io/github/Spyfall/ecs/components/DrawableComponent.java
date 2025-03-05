package io.github.Spyfall.ecs.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawableComponent implements Component{
    public enum DrawableType { SPRITE, ANIMATION }
    private DrawableType type;
    private TextureRegion region;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private Vector2 size;

    public DrawableType getType() {
        return type;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public TextureRegion getRegion() {
        return region;
    }
    public Vector2 getSize(){
        return this.size;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void incStateTime(){
        this.stateTime++;
    }

    //Bruke Sprite istedenfor textureregion? Forskjellen så langt jeg skjønner er at sprite kan ha posisjon + hvor langt den skal gå og at den scaler basert på det
    public DrawableComponent(DrawableType type, TextureRegion region, Animation<TextureRegion> animation, Vector2 size) {
        this.type = type;
        this.region = region;
        this.animation = animation;
        this.stateTime = 0.0f;
        this.size = size;
    }

}
