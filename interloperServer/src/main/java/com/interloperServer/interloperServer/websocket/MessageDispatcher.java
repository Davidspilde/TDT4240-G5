package com.interloperServer.interloperServer.websocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.websocket.handlers.WebSocketMessageHandler;

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

    public void dispatch(JsonNode json, WebSocketSession session) throws Exception {
        String type = json.get("type").asText();
        WebSocketMessageHandler<?> handler = handlers.get(type);

        if (handler == null) {
            session.sendMessage(new TextMessage("Unknown message type: " + type));
            return;
        }

        dispatchToHandler(handler, json, session);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe cast due to controlled getMessageClass()
    private void dispatchToHandler(WebSocketMessageHandler handler, JsonNode json, WebSocketSession session)
            throws Exception {
        Object message = objectMapper.treeToValue(json, handler.getMessageClass());
        handler.handle(message, session);
    }
}
