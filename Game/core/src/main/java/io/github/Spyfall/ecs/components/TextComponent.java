package io.github.Spyfall.ecs.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TextComponent implements Component{
    private final BitmapFont font = new BitmapFont(); // Shared font
    private String text;
    public TextComponent(String text, float scale) {
        this.text = text;
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);
    }

    public BitmapFont getFont(){
        return this.font;
    }

    public CharSequence getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
