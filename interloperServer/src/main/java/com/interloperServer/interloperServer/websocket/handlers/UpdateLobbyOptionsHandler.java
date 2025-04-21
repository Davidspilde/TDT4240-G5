package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.messages.incoming.ReceiveLobbyOptionsMessage;
import com.interloperServer.interloperServer.service.LobbyHostService;
import com.interloperServer.interloperServer.service.LobbyManagerService;

/**
 * Handles WebSocket messages of type "updateOptions".
 * <p>
 * This handler processes incoming messages that request updates to the lobby
 * options.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("updateOptions").</li>
 * <li>Specifies the class of the message it handles
 * ({@link ReceiveLobbyOptionsMessage}).</li>
 * <li>Invokes the
 * {@link LobbyHostService#updateLobbyOptions(Lobby, String, int, int, int, int, int)}
 * method to update the lobby options for the specified lobby.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class UpdateLobbyOptionsHandler implements WebSocketMessageHandler<ReceiveLobbyOptionsMessage> {

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
    public Class<ReceiveLobbyOptionsMessage> getMessageClass() {
        return ReceiveLobbyOptionsMessage.class;
    }

    @Override
    public void handle(ReceiveLobbyOptionsMessage message, WebSocketSession session) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(message.getLobbyCode());
        String username = message.getUsername();
        int roundLimit = message.getRoundLimit();
        int locationNumber = message.getLocationNumber();
        int timePerRound = message.getTimePerRound();
        int maxPlayerCount = message.getMaxPlayerCount();
        int spyLastAttemptTime = message.getSpyLastAttemptTime();

        hostService.updateLobbyOptions(lobby, username, roundLimit, locationNumber, timePerRound,
                maxPlayerCount, spyLastAttemptTime);
    }
}
