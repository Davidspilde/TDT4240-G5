package com.interloperServer.interloperServer.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.websocket.handlers.WebSocketMessageHandler;

/**
 * Dispatches incoming WebSocket messages to the appropriate handler.
 * <p>
 * This class is responsible for routing messages to the correct
 * {@link WebSocketMessageHandler}
 * based on the message type. It uses a map of handlers, where the key is the
 * message type,
 * and the value is the corresponding handler.
 * <p>
 * The dispatcher performs the following actions:
 * <ul>
 * <li>Registers all available {@link WebSocketMessageHandler} instances during
 * initialization.</li>
 * <li>Determines the message type from the incoming JSON payload.</li>
 * <li>Routes the message to the appropriate handler for processing.</li>
 * </ul>
 */
@Component
public class MessageDispatcher {
    private final Map<String, WebSocketMessageHandler<?>> handlers = new HashMap<>();
    private final ObjectMapper objectMapper;

    public MessageDispatcher(List<WebSocketMessageHandler<?>> handlerList, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        for (WebSocketMessageHandler<?> handler : handlerList) {
            handlers.put(handler.getType(), handler);
        }
    }

    /**
     * Dispatches an incoming WebSocket message to the appropriate handler.
     * <p>
     * Determines the message type from the JSON payload and routes it to the
     * corresponding handler.
     * If no handler is found for the message type, an error message is sent back to
     * the client.
     *
     * @param json    The incoming JSON payload as a {@link JsonNode}.
     * @param session The {@link WebSocketSession} associated with the client.
     * @throws Exception If an error occurs during message processing.
     */
    public void dispatch(JsonNode json, WebSocketSession session) throws Exception {
        String type = json.get("type").asText();
        WebSocketMessageHandler<?> handler = handlers.get(type);

        if (handler == null) {
            session.sendMessage(new TextMessage("Unknown message type: " + type));
            return;
        }

        dispatchToHandler(handler, json, session);
    }

    /**
     * Routes the message to the specified handler for processing.
     * <p>
     * Converts the JSON payload into the appropriate message class and invokes the
     * handler's
     * {@code handle} method.
     *
     * @param handler The {@link WebSocketMessageHandler} responsible for processing
     *                the message.
     * @param json    The incoming JSON payload as a {@link JsonNode}.
     * @param session The {@link WebSocketSession} associated with the client.
     * @throws Exception If an error occurs during message processing.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe cast due to controlled getMessageClass()
    private void dispatchToHandler(WebSocketMessageHandler handler, JsonNode json, WebSocketSession session)
            throws Exception {
        Object message = objectMapper.treeToValue(json, handler.getMessageClass());
        handler.handle(message, session);
    }
}
