package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.recievedMessages.RecieveMessage;
import com.interloperServer.interloperServer.service.GameService;

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
