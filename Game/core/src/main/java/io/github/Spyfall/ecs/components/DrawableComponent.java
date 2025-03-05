package io.github.Spyfall.ecs.components;

import com.badlogic.gdx.Gdx;
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
    private Sprite sprite;
    private int z_index;

    public DrawableComponent(DrawableType type, TextureRegion region, Animation<TextureRegion> animation, Vector2 size, int z_index) {
        this.z_index = z_index;
        this.type = type;
        this.region = region;
        this.sprite = new Sprite(region);
        if(size == null){
            size = new Vector2(region.getRegionWidth(),region.getRegionHeight());
        }
        this.sprite.setSize(size.x* Gdx.graphics.getWidth(),size.y*Gdx.graphics.getHeight());
        if(animation != null){
            this.animation = animation;
        }
    }

    public DrawableType getType() {
        return type;
    }

    public int getZ_index(){
        return z_index;
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

    private TextureRegion getAnimationFrame(){
        return animation.getKeyFrame(stateTime,true);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void changeFrame(){
        this.sprite.setRegion(getAnimationFrame());
    }
}
