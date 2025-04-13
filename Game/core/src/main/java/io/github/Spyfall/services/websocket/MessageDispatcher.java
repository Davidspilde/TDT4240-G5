package io.github.Spyfall.services.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.message.response.GameSpyCaughtMessage;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.message.response.GameVoteMessage;
import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class MessageDispatcher {
    private static MessageDispatcher instance;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketMessageHandler<?>> handlers = new HashMap<>();

    private MessageDispatcher() {
        objectMapper = new ObjectMapper();

        // Dynamicly fetches all handler classes
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
