package com.interloperServer.interloperServer.websocket;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.service.GameManagerService;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyManagerService;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final MessageDispatcher dispatcher;
    private final LobbyManagerService lobbyManager;
    private final GameService gameService;
    private final GameManagerService gameManagerService;
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;

    public GameWebSocketHandler(MessageDispatcher dispatcher, LobbyManagerService lobbyManager, GameService gameService,
            GameManagerService gameManagerService, MessagingService messagingService,
            GameMessageFactory messageFactory) {
        this.dispatcher = dispatcher;
        this.lobbyManager = lobbyManager;
        this.gameService = gameService;
        this.gameManagerService = gameManagerService;
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // If fields are missing from the message an error message will be sent
        try {
            String payload = message.getPayload();

            JsonNode root = new ObjectMapper().readTree(payload);
            dispatcher.dispatch(root, session);
        } catch (Exception e) {
            messagingService.sendMessage(session, messageFactory.error("There was an error parsing you message"));
        }
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        lobbyManager.removeUser(session);

        // Check if the user was in a game
        for (String lobbyCode : gameManagerService.getAllGameCodes()) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }
    }
}
