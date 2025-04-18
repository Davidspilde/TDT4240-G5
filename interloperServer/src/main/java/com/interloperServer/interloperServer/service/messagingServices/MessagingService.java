package com.interloperServer.interloperServer.service.messagingServices;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.Player;

/**
 * Service for handling WebSocket messaging.
 * <p>
 * This service provides methods to send JSON messages to individual players or
 * broadcast messages to all players in a lobby.
 * It also handles sending error messages in case of failures.
 */
@Service
public class MessagingService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a JSON message to all players in a game.
     *
     * @param lobby   The {@link Lobby} containing the players to whom the message
     *                will be sent.
     * @param message The message object to be serialized and sent.
     */
    public void broadcastMessage(Lobby lobby, Object message) {
        for (Player player : lobby.getPlayers()) {
            sendMessage(player.getSession(), message);
        }
    }

    /**
     * Sends a JSON message to a single player.
     *
     * @param session The {@link WebSocketSession} of the player to whom the message
     *                will be sent.
     * @param message The message object to be serialized and sent.
     */
    public void sendMessage(WebSocketSession session, Object message) {
        try {
            // Serialize the message object to JSON
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(session, "Failed to serialize message to JSON.");
        }
    }

    /**
     * Sends an error message to a single player.
     *
     * @param session      The {@link WebSocketSession} of the player to whom the
     *                     error message will be sent.
     * @param errorMessage The error description to be sent.
     */
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            String jsonError = objectMapper.writeValueAsString(
                    Map.of("event", "error", "message", errorMessage));
            session.sendMessage(new TextMessage(jsonError));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
