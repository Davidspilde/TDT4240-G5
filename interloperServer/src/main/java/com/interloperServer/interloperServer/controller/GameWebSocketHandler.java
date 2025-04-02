package com.interloperServer.interloperServer.controller;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.messages.CreateLobbyMessage;
import com.interloperServer.interloperServer.model.messages.LobbyOptionsMessage;
import com.interloperServer.interloperServer.model.messages.Message;
import com.interloperServer.interloperServer.model.messages.VoteMessage;
import com.interloperServer.interloperServer.service.GameManagerService;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyService;

/**
 * Receives all messages sent to /ws/game and delegates to the appropriate
 * services
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final GameService gameService;
    private final LobbyService lobbyService;
    private final GameManagerService gameManagerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameWebSocketHandler(GameService gameService, LobbyService lobbyService,
            GameManagerService gameManagerService) {
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.gameManagerService = gameManagerService;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    /**
     * Reads message and delegates to service
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.get("type").asText();

        switch (type) {
            /*
             * Message on the format:
             * 
             * {
             * "type": "createLobby",
             * "username": "Alice"
             * }
             * 
             */
            case "createLobby":
                CreateLobbyMessage createMsg = objectMapper.treeToValue(root, CreateLobbyMessage.class);
                lobbyService.createLobby(session, createMsg.getUsername());
                break;

            /*
             * Message on the format:
             * 
             * {
             * "type": "joinLobby",
             * "lobbyCode": "a9b7f9",
             * "username": "Bob"
             * }
             * 
             */
            case "joinLobby":
                Message joinMsg = objectMapper.treeToValue(root, Message.class);
                lobbyService.joinLobby(session, joinMsg.getLobbyCode(), joinMsg.getUsername());
                break;

            /*
             * Message on the format:
             * 
             * {
             * "type": "startGame",
             * "lobbyCode": "a9b7f9",
             * "username": "Alice"
             * }
             * 
             */
            case "startGame":
                Message startMsg = objectMapper.treeToValue(root, Message.class);
                gameService.startGame(startMsg.getUsername(),
                        lobbyService.getLobbyFromLobbyCode(startMsg.getLobbyCode()), session);
                break;

            /*
             * Message on the format:
             * 
             * {
             * "type": "vote",
             * "lobbyCode": "a9b7f9",
             * "username": "Bob",
             * "target": "Charlie"
             * }
             * 
             */
            case "vote":
                VoteMessage voteMsg = objectMapper.treeToValue(root, VoteMessage.class);
                gameService.castVote(voteMsg.getLobbyCode(), voteMsg.getUsername(), voteMsg.getTarget());
                break;

            /*
             * Message on the format:
             * 
             * {
             * "type": "spyVote",
             * "lobbyCode": "a9b7f9",
             * "username": "Bob",
             * "target": "Sauna"
             * }
             * 
             */
            case "spyVote":
                VoteMessage spyVoteMsg = objectMapper.treeToValue(root, VoteMessage.class);
                gameService.castSpyGuess(spyVoteMsg.getLobbyCode(), spyVoteMsg.getUsername(), spyVoteMsg.getTarget());
                break;

            /*
             * Message on the format:
             * 
             * {
             * "type": "advanceRound",
             * "lobbyCode": "a9b7f9",
             * "username": "Alice"
             * }
             * 
             */
            case "advanceRound":
                Message advanceMsg = objectMapper.treeToValue(root, Message.class);
                gameService.advanceRound(advanceMsg.getLobbyCode());
                break;
            /*
             * Message format:
             * {
             * "type": "updateOptions",
             * "lobbyCode": "a9b7f9",
             * "roundLimit": 8,
             * "locationNumber": 25,
             * "spyCount": 2,
             * "maxPlayerCount": 6,
             * "timePerRound": 120
             * }
             */
            case "updateOptions":
                LobbyOptionsMessage optionsMsg = objectMapper.treeToValue(root, LobbyOptionsMessage.class);
                lobbyService.updateLobbyOptions(optionsMsg.getLobbyCode(), optionsMsg);
                break;

            // When the message doesn't conform to any legal format
            default:
                session.sendMessage(new TextMessage("Unknown message type: " + type));
        }
    }

    /**
     * Removes player from lobby and game when they disconnect
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        lobbyService.removeUser(session);

        // Check if the user was in a game
        for (String lobbyCode : gameManagerService.getAllGameCodes()) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }

    }

}
