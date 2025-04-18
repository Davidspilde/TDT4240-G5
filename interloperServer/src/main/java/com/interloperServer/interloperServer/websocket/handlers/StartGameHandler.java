package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveMessage;
import com.interloperServer.interloperServer.service.GameService;

/**
 * Handles WebSocket messages of type "startGame".
 * <p>
 * This handler processes incoming messages that request the start of a game in
 * a specific lobby.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("startGame").</li>
 * <li>Specifies the class of the message it handles
 * ({@link RecieveMessage}).</li>
 * <li>Invokes the
 * {@link GameService#startGame(String, String, WebSocketSession)} method to
 * start the game for the specified lobby and user.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class StartGameHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final GameService gameService;

    public StartGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getType() {
        return "startGame";
    }

    @Override
    public Class<RecieveMessage> getMessageClass() {
        return RecieveMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {
        gameService.startGame(message.getUsername(), message.getLobbyCode(), session);
    }
}
