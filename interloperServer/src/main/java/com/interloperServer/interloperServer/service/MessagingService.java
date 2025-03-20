package com.interloperServer.interloperServer.service;


import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import org.springframework.stereotype.Service;


import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;

@Service
public class MessagingService {
    /**
     * Sends a message to all players in a game.
     */
    public void broadcastMessage(Game game, String message) {
        for (Player player : game.getPlayers()) {
            sendMessage(player.getSession(), message);
        }
    }

    /**
     * Sends a message to a single player.
     */
    public void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
