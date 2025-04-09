package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.recievedMessages.RecieveVoteMessage;
import com.interloperServer.interloperServer.service.GameService;

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
