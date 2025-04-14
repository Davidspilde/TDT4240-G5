package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.messages.incomming.RecieveLobbyOptionsMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LobbyManagerService Tests")
public class LobbyManagerServiceTest {

    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private LobbyManagerService lobbyService;
    private LobbyHostService lobbyHostservice;
    private WebSocketSession session;

    @BeforeEach
    public void setup() {
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        session = mock(WebSocketSession.class);
        lobbyHostservice = mock(LobbyHostService.class);
        lobbyService = new LobbyManagerService(messagingService, messageFactory, lobbyHostservice);
    }

    @Test
    @DisplayName("Should create a new lobby and assign host correctly")
    public void testCreateLobby() {
        String username = "hostUser";
        String lobbyCode = lobbyService.createLobby(session, username);

        Lobby lobby = lobbyService.getLobbyFromLobbyCode(lobbyCode);
        assertNotNull(lobby);
        assertEquals(username, lobby.getHost().getUsername());
        assertTrue(lobby.getPlayers().contains(lobby.getHost()));
    }

    @Test
    @DisplayName("Should allow a player to join an existing lobby")
    public void testJoinLobby_Success() {
        String host = "host";
        String guest = "guest";

        String lobbyCode = lobbyService.createLobby(session, host);
        WebSocketSession guestSession = mock(WebSocketSession.class);

        boolean result = lobbyService.joinLobby(guestSession, lobbyCode, guest);

        assertTrue(result);
        List<String> usernames = lobbyService.getPlayersInLobby(lobbyCode).stream().map(p -> p.getUsername()).toList();
        assertTrue(usernames.containsAll(List.of(host, guest)));
    }

    @Test
    @DisplayName("Should reject join if lobby is not found")
    public void testJoinLobby_LobbyNotFound() {
        WebSocketSession newSession = mock(WebSocketSession.class);
        boolean result = lobbyService.joinLobby(newSession, "INVALID", "someone");

        assertFalse(result);
        verify(messagingService).sendMessage(eq(newSession), any());
    }

    @Test
    @DisplayName("Should remove player and delete lobby if empty")
    public void testRemoveUser_RemovesPlayerAndLobbyIfEmpty() {
        String username = "host";
        String lobbyCode = lobbyService.createLobby(session, username);

        lobbyService.removeUser(session);
        Lobby removedLobby = lobbyService.getLobbyFromLobbyCode(lobbyCode);

        assertNull(removedLobby);
    }

    @Test
    @DisplayName("Should correctly identify if user is host")
    public void testIsHost() {
        String username = "host";
        String lobbyCode = lobbyService.createLobby(session, username);

        assertTrue(lobbyService.isHost(lobbyCode, username));
        assertFalse(lobbyService.isHost(lobbyCode, "notHost"));
    }
}
