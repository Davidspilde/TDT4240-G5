package io.github.Spyfall.Model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    private Texture texture;
    private Vector2 pos;
    private BitmapFont font;
    private String text;
    private Rectangle boundingBox;
    private float scaleX,scaleY;

    public Button(Texture texture, Vector2 pos, String text) {
        this.texture = texture;
        this.pos = pos;
        this.font = new BitmapFont();
        this.text = text;
        this.scaleX = 0.1F;
        this.scaleY = 0.1F;
        this.boundingBox = new Rectangle(pos.x, pos.y, texture.getWidth()*scaleX,texture.getHeight()*scaleY);
    }

    public boolean isPressed(float touchX, float touchY) {
        return boundingBox.contains(touchX, touchY);
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture,pos.x,pos.y, texture.getWidth()*scaleX,texture.getHeight()*scaleY);
        font.getData().setScale(3);
        // Draw the text (centered on the button)
        float textWidth = font.getRegion().getRegionWidth(); // Get text width
        float textHeight = font.getLineHeight(); // Get text height
        float textX = pos.x + (float) (texture.getWidth()*scaleX) / 3; // Center the text horizontally
        float textY = pos.y + (float) (texture.getHeight()*scaleY) / 2; // Center the text vertically
        font.draw(batch, text, textX, textY);
    }

    public Vector2 getPos() {
        return pos;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public BitmapFont getFont(){
       return font;
    }

    public String getText() {
        return text;
    }

    public Texture getTexture() {
        return texture;
    }
}
