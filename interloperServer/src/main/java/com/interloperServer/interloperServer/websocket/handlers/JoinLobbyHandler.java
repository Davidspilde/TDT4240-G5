package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.ReceiveMessage;
import com.interloperServer.interloperServer.service.LobbyManagerService;

/**
 * Handles WebSocket messages of type "joinLobby".
 * <p>
 * This handler processes incoming messages that request a player to join a
 * lobby.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("joinLobby").</li>
 * <li>Specifies the class of the message it handles
 * ({@link ReceiveMessage}).</li>
 * <li>Invokes the
 * {@link LobbyManagerService#joinLobby(WebSocketSession, String, String)}
 * method to add the player to the specified lobby.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class JoinLobbyHandler implements WebSocketMessageHandler<ReceiveMessage> {

    private final LobbyManagerService lobbyManager;

    public JoinLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "joinLobby";
    }

    @Override
    public Class<ReceiveMessage> getMessageClass() {
        return ReceiveMessage.class;
    }

    @Override
    public void handle(ReceiveMessage message, WebSocketSession session) {
        lobbyManager.joinLobby(session, message.getLobbyCode(), message.getUsername());
    }
}
