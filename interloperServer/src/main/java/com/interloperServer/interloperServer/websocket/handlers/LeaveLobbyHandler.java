package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.RecieveMessage;
import com.interloperServer.interloperServer.service.LobbyManagerService;

/**
 * Handles WebSocket messages of type "leaveLobby".
 * <p>
 * This handler processes incoming messages that request a player to leave a
 * lobby.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("leaveLobby").</li>
 * <li>Specifies the class of the message it handles
 * ({@link RecieveMessage}).</li>
 * <li>Invokes the
 * {@link LobbyManagerService#leaveLobby(WebSocketSession, String, String)}
 * method to remove the player from the specified lobby.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class LeaveLobbyHandler implements WebSocketMessageHandler<RecieveMessage> {

    private final LobbyManagerService lobbyManager;

    public LeaveLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "leaveLobby";
    }

    @Override
    public Class<RecieveMessage> getMessageClass() {
        return RecieveMessage.class;
    }

    @Override
    public void handle(RecieveMessage message, WebSocketSession session) {
        lobbyManager.leaveLobby(session, message.getLobbyCode(), message.getUsername());
    }
}
