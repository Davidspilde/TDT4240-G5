
package io.github.Spyfall.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

//Loads all the assets which we use like backgrounds and fonts
public class AssetLoader {

    private static final String VOTE_BG = "images/guess_bg.png";
    private static final String MAIN_BG = "images/Background_city.png";
    private static final String FONT_PATH = "fonts/font.fnt";

    private static AssetManager assetManager;
    public static BitmapFont font;

    public static Texture voteBackground;
    public static Texture mainBackground;

    public static void load() {
        assetManager = new AssetManager();

        // Load fonts and backgrounds
        assetManager.load(FONT_PATH, BitmapFont.class);
        assetManager.load(VOTE_BG, Texture.class);
        assetManager.load(MAIN_BG, Texture.class);

        assetManager.finishLoading();

        // Fetch font
        font = assetManager.get(FONT_PATH, BitmapFont.class);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.setUseIntegerPositions(false);

        // Fetch textures
        voteBackground = assetManager.get(VOTE_BG, Texture.class);
        mainBackground = assetManager.get(MAIN_BG, Texture.class);
    }

    public static void dispose() {
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
}
