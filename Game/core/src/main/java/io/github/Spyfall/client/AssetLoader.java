package io.github.Spyfall.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class AssetLoader {

    private static AssetManager assetManager;
    public static BitmapFont font;

    public static void load() {
        assetManager = new AssetManager();
        assetManager.load("fonts/font.fnt", BitmapFont.class);
        assetManager.finishLoading();

        font = assetManager.get("fonts/font.fnt",BitmapFont.class);

         // Prevent blurry fonts
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.setUseIntegerPositions(false);
    }

    public static void dispose() {
        assetManager.dispose();
    }



    
    
}
