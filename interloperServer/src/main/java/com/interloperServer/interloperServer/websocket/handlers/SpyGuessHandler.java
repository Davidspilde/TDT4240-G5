package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.recievedMessages.RecieveVoteMessage;
import com.interloperServer.interloperServer.service.GameService;

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
