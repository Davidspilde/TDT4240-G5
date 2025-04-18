package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveVoteMessage;
import com.interloperServer.interloperServer.service.GameService;

/**
 * Handles WebSocket messages of type "spyGuess".
 * <p>
 * This handler processes incoming messages where the spy makes a guess about
 * the location.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("spyGuess").</li>
 * <li>Specifies the class of the message it handles
 * ({@link RecieveVoteMessage}).</li>
 * <li>Invokes the {@link GameService#castSpyGuess(String, String, String)}
 * method to process the spy's guess.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class SpyGuessHandler implements WebSocketMessageHandler<RecieveVoteMessage> {

    private final GameService gameService;

    public SpyGuessHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getType() {
        return "spyGuess";
    }

    @Override
    public Class<RecieveVoteMessage> getMessageClass() {
        return RecieveVoteMessage.class;
    }

    @Override
    public void handle(RecieveVoteMessage message, WebSocketSession session) {
        gameService.castSpyGuess(message.getLobbyCode(), message.getUsername(), message.getTarget());
    }
}
