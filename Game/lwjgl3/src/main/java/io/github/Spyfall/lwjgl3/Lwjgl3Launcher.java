package io.github.Spyfall.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.github.Spyfall.Main;
import io.github.Spyfall.client.Config;
import io.github.cdimascio.dotenv.Dotenv;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        String uri = getWebSocketUriDesktop();
        Config.setWebSocketUri(uri);
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Spyfall");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        configuration.setWindowSizeLimits(720,1280,720,1280);
        return configuration;
    }

    private static String getWebSocketUriDesktop() {
        String defaultUri = "ws://localhost:8080/ws/game";
        try {
            Dotenv dotenv = Dotenv.configure()
                .directory("../") // adjust based on your setup
                .ignoreIfMissing() // important to not crash if not found
                .load();

            return dotenv.get("SPYFALL_WS_URI", defaultUri); // ✅ fallback inside dotenv
        } catch (Exception e) {
            System.out.println("Could not load .env, using default URI");
            return defaultUri;
        }
    }
}
