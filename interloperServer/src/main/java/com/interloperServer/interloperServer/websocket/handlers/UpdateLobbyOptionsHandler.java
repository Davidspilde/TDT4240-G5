package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.messages.incoming.RecieveLobbyOptionsMessage;
import com.interloperServer.interloperServer.service.LobbyHostService;
import com.interloperServer.interloperServer.service.LobbyManagerService;

@Component
public class UpdateLobbyOptionsHandler implements WebSocketMessageHandler<RecieveLobbyOptionsMessage> {

    private final LobbyHostService hostService;
    private final LobbyManagerService lobbyManager;

    public UpdateLobbyOptionsHandler(LobbyHostService hostService, LobbyManagerService lobbyManager) {
        this.hostService = hostService;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "updateOptions";
    }

    @Override
    public Class<RecieveLobbyOptionsMessage> getMessageClass() {
        return RecieveLobbyOptionsMessage.class;
    }

    @Override
    public void handle(RecieveLobbyOptionsMessage message, WebSocketSession session) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(message.getLobbyCode());
        int roundLimit = message.getRoundLimit();
        int spyCount = message.getSpyCount();
        int locationNumber = message.getLocationNumber();
        int timePerRound = message.getTimePerRound();
        int maxPlayerCount = message.getMaxPlayerCount();
        int spyLastAttemptTime = message.getSpyLastAttemptTime();

        hostService.updateLobbyOptions(lobby, roundLimit, spyCount, locationNumber, timePerRound,
                maxPlayerCount, spyLastAttemptTime);
    }
}
