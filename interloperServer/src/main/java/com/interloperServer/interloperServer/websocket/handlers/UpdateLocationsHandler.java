package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.messages.incoming.ReceiveLocationsMessage;
import com.interloperServer.interloperServer.service.LobbyHostService;
import com.interloperServer.interloperServer.service.LobbyManagerService;

/**
 * Handles WebSocket messages of type "updateLocations".
 * <p>
 * This handler processes incoming messages that request updates to the list of
 * locations in a lobby.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("updateLocations").</li>
 * <li>Specifies the class of the message it handles
 * ({@link ReceiveLocationsMessage}).</li>
 * <li>Invokes the {@link LobbyHostService#setLocations(Lobby, List, String)}
 * method to update the locations for the specified lobby.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class UpdateLocationsHandler implements WebSocketMessageHandler<ReceiveLocationsMessage> {

    private final LobbyHostService hostService;
    private final LobbyManagerService lobbyManager;

    public UpdateLocationsHandler(LobbyHostService hostService, LobbyManagerService lobbyManager) {
        this.hostService = hostService;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "updateLocations";
    }

    @Override
    public Class<ReceiveLocationsMessage> getMessageClass() {
        return ReceiveLocationsMessage.class;
    }

    @Override
    public void handle(ReceiveLocationsMessage message, WebSocketSession session) {

        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(message.getLobbyCode());

        hostService.setLocations(lobby, message.getLocations(), message.getUsername());
    }
}
