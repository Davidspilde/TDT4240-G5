package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.messages.incoming.ReceiveCreateLobbyMessage;
import com.interloperServer.interloperServer.service.LobbyManagerService;

/**
 * Handles WebSocket messages of type "createLobby".
 * <p>
 * This handler processes incoming messages that request the creation of a new
 * lobby.
 * <p>
 * The handler performs the following actions:
 * <ul>
 * <li>Retrieves the type of the message it processes ("createLobby").</li>
 * <li>Specifies the class of the message it handles
 * ({@link ReceiveCreateLobbyMessage}).</li>
 * <li>Invokes the
 * {@link LobbyManagerService#createLobby(WebSocketSession, String)} method to
 * create a new lobby for the specified user.</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link WebSocketMessageHandler}, which defines the
 * required methods for handling WebSocket messages.
 */
@Component
public class CreateLobbyHandler implements WebSocketMessageHandler<ReceiveCreateLobbyMessage> {

    private final LobbyManagerService lobbyManager;

    public CreateLobbyHandler(LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public String getType() {
        return "createLobby";
    }

    @Override
    public Class<ReceiveCreateLobbyMessage> getMessageClass() {
        return ReceiveCreateLobbyMessage.class;
    }

    @Override
    public void handle(ReceiveCreateLobbyMessage message, WebSocketSession session) {
        lobbyManager.createLobby(session, message.getUsername());
    }
}
