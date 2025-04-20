package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.ReceiveMessage;
import com.interloperServer.interloperServer.service.GameService;

/**
 * Handles WebSocket messages of type "advanceRound".
 * <p>
 * This handler processes incoming messages that request advancing the game to
 * the next round.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("advanceRound").</li>
 * <li>Specifies the class of the message it handles
 * ({@link ReceiveMessage}).</li>
 * <li>Invokes the {@link GameService#advanceRound(String)} method to advance
 * the round for the specified lobby.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class AdvanceRoundHandler implements WebSocketMessageHandler<ReceiveMessage> {

    private final GameService gameService;

    public AdvanceRoundHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getType() {
        return "advanceRound";
    }

    @Override
    public Class<ReceiveMessage> getMessageClass() {
        return ReceiveMessage.class;
    }

    @Override
    public void handle(ReceiveMessage message, WebSocketSession session) {
        gameService.advanceRound(message.getLobbyCode());
    }
}
