package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.messages.incomming.RecieveLocationsMessage;
import com.interloperServer.interloperServer.service.LobbyHostService;
import com.interloperServer.interloperServer.service.LobbyManagerService;

@Component
public class UpdateLocationsHandler implements WebSocketMessageHandler<RecieveLocationsMessage> {

    private final LobbyHostService hostService;
    private final LobbyManagerService lobbyManager;

    public UpdateLocationsHandler(LobbyHostService hostService, LobbyManagerService lobbyManager) {
        this.hostService = hostService;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "updateOptions";
    }

    @Override
    public Class<RecieveLocationsMessage> getMessageClass() {
        return RecieveLocationsMessage.class;
    }

    @Override
    public void handle(RecieveLocationsMessage message, WebSocketSession session) {

        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(message.getLobbyCode());

        hostService.setLocations(lobby, message.getLocations(), message.getUsername());
    }
}
