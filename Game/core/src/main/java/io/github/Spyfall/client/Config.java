package io.github.Spyfall.client;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    public static String getWebSocketUri() {
        String defaultUri = "ws://localhost:8080/ws/game";
        System.out.println("Working directory: " + System.getProperty("user.dir"));
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
