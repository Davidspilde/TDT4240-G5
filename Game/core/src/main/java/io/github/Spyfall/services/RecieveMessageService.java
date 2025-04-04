package io.github.Spyfall.services;

import com.badlogic.gdx.utils.Json;

public class RecieveMessageService {
    private static RecieveMessageService instance;

    private RecieveMessageService() {
    }

    public static RecieveMessageService GetInstance() {
        if (instance == null) {
            instance = new RecieveMessageService();
        }
        return instance;
    }

    public void handleMessage(Json message) {
    }
}
