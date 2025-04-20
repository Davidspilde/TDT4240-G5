package io.github.Spyfall.services.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class MessageDispatcher {
    private static MessageDispatcher instance;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketMessageHandler<?>> handlers = new HashMap<>();

    private MessageDispatcher() {
        objectMapper = new ObjectMapper();

        // Dynamically fetches all handler classes
        Reflections reflections = new Reflections("io.github.Spyfall.services.websocket.handlers");

        Set<Class<? extends WebSocketMessageHandler>> handlerClasses = reflections
                .getSubTypesOf(WebSocketMessageHandler.class);

        // Creates a map of all handlers
        try {
            for (Class<? extends WebSocketMessageHandler> handlerClass : handlerClasses) {

                WebSocketMessageHandler<?> handler = handlerClass.getDeclaredConstructor().newInstance();
                handlers.put(handler.getEvent(), handler);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static MessageDispatcher GetInstance() {
        if (instance == null) {
            instance = new MessageDispatcher();
        }
        return instance;
    }

    public void dispatch(String msg) {

        try {
            JsonNode json = objectMapper.readTree(msg);

            String event = json.get("event").asText();
            WebSocketMessageHandler<?> handler = handlers.get(event);

            // Checks if there exists handler for given event
            if (handler == null) {
                System.out.println("No handler registered for event type: " + event);
                return;
            }
            dispatchToHandler(handler, json);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe cast due to controlled getMessageClass()
    private void dispatchToHandler(WebSocketMessageHandler handler, JsonNode json) throws Exception {

        Object message = objectMapper.treeToValue(json, handler.getMessageClass());

        handler.handle(message);
    }
}
