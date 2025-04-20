package com.interloperServer.interloperServer.websocket;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.service.GameConnectionService;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/**
 * Handles WebSocket communication for the game.
 * <p>
 * This class extends {@link TextWebSocketHandler} to process WebSocket events
 * such as incoming messages,
 * connection establishment, and connection closure.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Processes incoming text messages and dispatches them to the appropriate
 * handler using {@link MessageDispatcher}.</li>
 * <li>Handles connection establishment by invoking
 * {@link GameConnectionService#onConnect(WebSocketSession)}.</li>
 * <li>Handles connection closure by invoking
 * {@link GameConnectionService#onDisconnect(WebSocketSession)}.</li>
 * </ul>
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameConnectionService connectionService;
    private final MessageDispatcher dispatcher;
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;

    public GameWebSocketHandler(MessageDispatcher dispatcher,
            MessagingService messagingService,
            GameMessageFactory messageFactory, GameConnectionService connectionService) {
        this.dispatcher = dispatcher;
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;

        this.connectionService = connectionService;
    }

    /**
     * Handles incoming text messages from the WebSocket connection.
     * <p>
     * Parses the JSON payload and dispatches it to the appropriate handler using
     * {@link MessageDispatcher}.
     * If an error occurs during parsing, an error message is sent back to the
     * client.
     *
     * @param session The {@link WebSocketSession} associated with the client.
     * @param message The {@link TextMessage} received from the client.
     * @throws Exception If an error occurs during message processing.
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();

            JsonNode root = new ObjectMapper().readTree(payload);
            dispatcher.dispatch(root, session);
        } catch (Exception e) {
            messagingService.sendMessage(session, messageFactory.error("There was an error parsing your message"));
        }
    }

    /**
     * Handles the event when a WebSocket connection is established.
     * <p>
     * Invokes {@link GameConnectionService#onConnect(WebSocketSession)} to handle
     * connection logic.
     *
     * @param session The {@link WebSocketSession} associated with the client.
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        connectionService.onConnect(session);
    }

    /**
     * Handles the event when a WebSocket connection is closed.
     * <p>
     * Invokes {@link GameConnectionService#onDisconnect(WebSocketSession)} to
     * handle disconnection logic.
     *
     * @param session The {@link WebSocketSession} associated with the client.
     * @param status  The {@link CloseStatus} indicating the reason for the
     *                connection closure.
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        connectionService.onDisconnect(session);
    }
}
