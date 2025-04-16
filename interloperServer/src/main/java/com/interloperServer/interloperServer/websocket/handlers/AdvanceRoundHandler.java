package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveMessage;
import com.interloperServer.interloperServer.service.GameService;

@Component
public class AdvanceRoundHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final GameService gameService;

    public AdvanceRoundHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getType() {
        return "advanceRound";
    }

    @Override
    public Class<RecieveMessage> getMessageClass() {
        return RecieveMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {
        gameService.advanceRound(message.getLobbyCode());
    }
}
