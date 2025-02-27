package io.github.Spyfall.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.Spyfall.Main;
import io.github.Spyfall.Model.Button;
import io.github.Spyfall.Controller.GameStateManager;

public class MainMenuState extends State {

    // Declare the textures for the logo, buttons, and icons
    private final Texture logo;
    private final Button createGameButton, joinGameButton, tutorialButton;
    private Texture buttonTexture;
    private final Texture icon1;
    private final Texture icon2;
    private final Texture icon3;
    private final SpriteBatch spriteBatch;
    private int width,height;

    public MainMenuState(GameStateManager gameStateManager) {
        super(gameStateManager);
        spriteBatch = new SpriteBatch();
        width = Main.WIDTH;
        height= Main.HEIGHT;
        camera.setToOrtho(false,width,height);  // Set the camera to match screen dimensions

        // Load the logo and buttons
        logo = new Texture("logo.png");  // Replace with your logo file

        // Create buttons with appropriate positions and textures
        buttonTexture = new Texture("Background.png");
        createGameButton = new Button(buttonTexture, new Vector2(100, height-1000/3), "Create Game");
//        buttonTexture = new Texture("join_game_button.png");
        joinGameButton = new Button(buttonTexture, new Vector2(100, height - 1400/3), "Join Game");
//        buttonTexture = new Texture("tutorial_button.png");
        tutorialButton = new Button(buttonTexture, new Vector2(100, height-1800/3), "Tutorial");

        // Load icons for the bottom of the screen
        icon1 = new Texture("icon1.png");  // Replace with your icon file
        icon2 = new Texture("icon2.png");  // Replace with your icon file
        icon3 = new Texture("icon3.png");  // Replace with your icon file
    }

    @Override
    public void update(float dt) {
        // Convert screen coordinates to world coordinates
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);  // Convert to world coordinates using the camera

        // Check if any button is pressed
        if (Gdx.input.isTouched()) {
            // Handle button presses
            if (createGameButton.isPressed(touchPos.x, touchPos.y)) {
                System.out.println("Create Game Button Pressed");
                // Transition to the game screen (example, implement it)
            } else if (joinGameButton.isPressed(touchPos.x, touchPos.y)) {
                System.out.println("Join Game Button Pressed");
                // Transition to the join game screen (example, implement it)
            } else if (tutorialButton.isPressed(touchPos.x, touchPos.y)) {
                System.out.println("Tutorial Button Pressed");
                // Transition to the tutorial screen (example, implement it)
            }
        }
    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Draw the background, logo, and buttons
        spriteBatch.draw(logo, (float) (Main.WIDTH - logo.getWidth()) / 2, Main.HEIGHT - logo.getHeight() - 20);  // Center the logo at the top

        // Draw the buttons
        createGameButton.draw(spriteBatch);
        joinGameButton.draw(spriteBatch);
        tutorialButton.draw(spriteBatch);

        // Draw icons at the bottom
        spriteBatch.draw(icon1,0, 0,(float)icon1.getWidth()/4,(float)icon1.getHeight()/4);  // Position icon1
        spriteBatch.draw(icon2, ((float) Main.WIDTH / 2) - ((float) icon2.getWidth() / 4), 0,(float)icon1.getWidth()/4,(float)icon1.getHeight()/4);  // Position icon2
        spriteBatch.draw(icon3, Main.WIDTH-((float)icon3.getWidth()/4), 0,(float)icon3.getWidth()/4,(float)icon3.getHeight()/4);  // Position icon3
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        // Dispose all textures to avoid memory leaks
        logo.dispose();
        createGameButton.getTexture().dispose();
        joinGameButton.getTexture().dispose();
        tutorialButton.getTexture().dispose();
        icon1.dispose();
        icon2.dispose();
        icon3.dispose();
        spriteBatch.dispose();
    }
}
