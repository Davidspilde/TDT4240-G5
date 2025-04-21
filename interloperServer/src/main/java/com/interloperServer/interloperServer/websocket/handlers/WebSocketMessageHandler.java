package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a generic WebSocket message handler interface for processing
 * incoming messages.
 * <p>
 * This interface defines the following methods:
 * <ul>
 * <li><b>getType()</b>: Returns the type of the message this handler processes
 * (e.g., "createLobby", "vote").</li>
 * <li><b>getMessageClass()</b>: Returns the class of the message this handler
 * processes, used for deserialization.</li>
 * <li><b>handle()</b>: Processes the incoming message and performs the
 * necessary actions.</li>
 * </ul>
 * 
 * <p>
 * Implementations of this interface are responsible for handling specific
 * message types and their associated logic.
 */
public interface WebSocketMessageHandler<T> {
    String getType(); // e.g. "createLobby", "vote", etc.

    // which message class that should be used
    Class<T> getMessageClass();

    void handle(T message, WebSocketSession session);
}
