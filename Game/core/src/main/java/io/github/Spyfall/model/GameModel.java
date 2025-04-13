package io.github.Spyfall.model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private List<GameStateObserver> observers = new ArrayList<>();
    private GameState currentState = GameState.MAIN_MENU;
    private LobbyData lobbyData;
    private GameData gameData;
    private String username;
    private String lobbyCode;

    // Singleton instance
    private static GameModel instance;
    
    private GameModel() {
        lobbyData = new LobbyData();
        gameData = new GameData();
    }
    
    public static GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }
        return instance;
    }

    // Observer pattern implementation
    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers() {
        // Create a copy of the observers list to avoid concurrent modification
        List<GameStateObserver> observersCopy = new ArrayList<>(observers);
        for (GameStateObserver observer : observersCopy) {
            observer.onGameStateChanged(this);
        }
    }
    
    public GameState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(GameState state) {
        this.currentState = state;
        notifyObservers();
    }
    
    public LobbyData getLobbyData() {
        return lobbyData;
    }
    
    public GameData getGameData() {
        return gameData;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        notifyObservers();
    }
    
    public String getLobbyCode() {
        return lobbyCode;
    }
    
    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
        notifyObservers();
    }
}
