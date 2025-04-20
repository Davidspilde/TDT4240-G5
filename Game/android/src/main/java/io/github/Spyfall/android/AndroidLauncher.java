package io.github.Spyfall.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

import android.util.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.github.Spyfall.Main;
import io.github.Spyfall.client.Config;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        String wsUri = loadWebSocketUriFromAssets();

        Config.setWebSocketUri(wsUri);
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new Main(), configuration);

    }

    private String loadWebSocketUriFromAssets() {
        try {
            InputStream input = getAssets().open("config.properties");
            Properties props = new Properties();
            props.load(input);
            return props.getProperty("SPYFALL_WS_URI", "ws://localhost:8080/ws/game");
        } catch (IOException e) {
            return "ws://localhost:8080/ws/game";
        }
    }
}
