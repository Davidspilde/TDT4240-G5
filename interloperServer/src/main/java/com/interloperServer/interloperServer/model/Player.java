package com.interloperServer.interloperServer.model;

import org.springframework.web.socket.WebSocketSession;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a player in the game.
 * <p>
 * This class manages the player's WebSocket session, username, connection
 * status,
 * and logic for handling disconnections. It provides methods for scheduling and
 * canceling removal tasks when a player disconnects.
 */
public class Player {

    private WebSocketSession session;
    private final String username;
    private boolean disconnected;

    // Timer for scheduling removal if the user doesn't rejoin
    private transient Timer disconnectTimer;

    public Player(WebSocketSession session, String username) {
        this.session = session;
        this.username = username;
        this.disconnected = false; // Default is connected
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * Schedule removal of this player when they disconnect
     * 
     * @param removalTask     The task to be run when the removal timer reaches zero
     * @param bufferInSeconds How long the timer should wait before running the task
     */
    public synchronized void scheduleDisconnectRemoval(Runnable removalTask, int bufferInSeconds) {
        // Cancel any old timer, if one exists
        if (disconnectTimer != null) {
            disconnectTimer.cancel();
        }

        this.disconnected = true;

        // Schedule new removal timer
        disconnectTimer = new Timer();
        disconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If the player is still disconnected when this fires, call the removal logic
                if (disconnected) {
                    removalTask.run();
                }
            }
        }, bufferInSeconds * 1000);
    }

    /**
     * Cancel an already begun disconnect timer
     */
    public synchronized void cancelDisconnectRemoval() {
        this.disconnected = false;

        if (disconnectTimer != null) {
            disconnectTimer.cancel();
            disconnectTimer = null;
        }
    }
}
