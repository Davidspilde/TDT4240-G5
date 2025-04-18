package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveVoteMessage;
import com.interloperServer.interloperServer.service.GameService;

/**
 * Handles WebSocket messages of type "vote".
 * <p>
 * This handler processes incoming messages where a player casts a vote for
 * another player.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("vote").</li>
 * <li>Specifies the class of the message it handles
 * ({@link RecieveVoteMessage}).</li>
 * <li>Invokes the {@link GameService#castVote(String, String, String)} method
 * to process the vote for the specified target player.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class VoteHandler implements WebSocketMessageHandler<RecieveVoteMessage> {

    private final GameService gameService;

    public VoteHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getType() {
        return "vote";
    }

    @Override
    public Class<RecieveVoteMessage> getMessageClass() {
        return RecieveVoteMessage.class;
    }

    @Override
    public void handle(RecieveVoteMessage message, WebSocketSession session) {
        gameService.castVote(message.getLobbyCode(), message.getUsername(), message.getTarget());
    }
}
