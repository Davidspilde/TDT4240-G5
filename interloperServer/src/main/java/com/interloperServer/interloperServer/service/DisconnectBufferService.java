package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class DisconnectBufferService {

    // Grace period (in milliseconds) during which a disconnected user may
    // reconnect.
    private static final long GRACE_PERIOD_MS = 30000; // 30 seconds

    // Scheduled executor allows many tasks to be scheduled concurrently.
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    // Map keyed by username for pending removal tasks.
    private final ConcurrentHashMap<String, ScheduledFuture<?>> pendingRemovals = new ConcurrentHashMap<>();

    private final GameService gameService;

    public DisconnectBufferService(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Schedule a removal task that will run after the grace period.
     * If the user does not reconnect within this time, forcedRemoval() is called.
     *
     * @param username  The username of the disconnecting user
     * @param session   The WebSocketSession that just closed
     * @param lobbyCode The lobby code the user belongs to
     */
    public void scheduleRemoval(String username, WebSocketSession session, String lobbyCode) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            forcedRemoval(username, lobbyCode, session);
        }, GRACE_PERIOD_MS, TimeUnit.MILLISECONDS);
        pendingRemovals.put(username, future);
    }

    /**
     * Cancel a pending removal if the user reconnects.
     *
     * @param username The username of the user reconnecting.
     */
    public void cancelRemoval(String username) {
        ScheduledFuture<?> future = pendingRemovals.remove(username);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * Called when the grace period expires. This method performs the actual removal
     * from both the lobby and the game.
     *
     * @param username  The username of the user to remove.
     * @param lobbyCode The lobby code where the user is present.
     * @param session   The WebSocketSession that was disconnected.
     */
    private void forcedRemoval(String username, String lobbyCode, WebSocketSession session) {
        pendingRemovals.remove(username);
        gameService.handlePlayerDisconnect(session, lobbyCode);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
