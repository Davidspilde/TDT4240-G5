package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LobbyServiceTest {

    @InjectMocks
    private LobbyService lobbyService;

    @Mock
    private MessagingService messagingService;

    @Mock
    private WebSocketSession session1;

    @Mock
    private WebSocketSession session2;

    private GameMessageFactory messageFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageFactory = new GameMessageFactory();
    }

    @Test
    @DisplayName("Creating a new lobby should assign the creator as host")
    public void createLobby_assignsHost() throws Exception {
        String username = "Player1";
        String lobbyCode = lobbyService.createLobby(session1, username);

        assertNotNull(lobbyCode);

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(1, players.size());
        assertEquals(username, players.get(0).getUsername());
        assertEquals(lobbyService.getLobbyFromLobbyCode(lobbyCode).getHost(), players.get(0));

        verify(messagingService).sendMessage(eq(session1),
                eq(messageFactory.lobbyCreated(lobbyCode, username)));
    }

    @Test
    @DisplayName("Joining existing lobby should add new player")
    public void joinLobby_addsPlayer() throws Exception {
        String lobbyCode = lobbyService.createLobby(session1, "Player1");

        boolean joined = lobbyService.joinLobby(session2, lobbyCode, "Player2");
        assertTrue(joined);

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(2, players.size());
        assertEquals("Player2", players.get(1).getUsername());

        List<String> playerNames = List.of("Player1", "Player2");

        verify(messagingService).sendMessage(eq(session2),
                eq(messageFactory.joinedLobby(lobbyCode, "Player1")));

        verify(messagingService).sendMessage(eq(session1),
                eq(messageFactory.lobbyUpdate(playerNames)));

        verify(messagingService).sendMessage(eq(session2),
                eq(messageFactory.lobbyUpdate(playerNames)));
    }

    @Test
    @DisplayName("Joining non-existent lobby should send error message")
    public void joinLobby_invalidCode() throws Exception {
        boolean joined = lobbyService.joinLobby(session2, "fakeCode", "Player2");
        assertFalse(joined);

        verify(messagingService).sendMessage(eq(session2),
                eq(messageFactory.error("Lobby not found!")));
    }

    @Test
    @DisplayName("Removing user should remove them from lobby. If host leaves, new host is assigned.")
    public void removeUserTest() {
        String lobbyCode = lobbyService.createLobby(session1, "Player1");
        lobbyService.joinLobby(session2, lobbyCode, "Player2");

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(2, players.size());

        // Remove host
        lobbyService.removeUser(session1);

        players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(1, players.size());
        assertEquals("Player2", players.get(0).getUsername());
        assertEquals(lobbyService.getLobbyFromLobbyCode(lobbyCode).getHost(), players.get(0));

        verify(messagingService).sendMessage(eq(session2),
                eq(messageFactory.newHost("Player2")));

        // Remove the last player
        lobbyService.removeUser(session2);
        players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(0, players.size());
    }

    @Test
    @DisplayName("broadcastPlayerList should send updated list of usernames to all players")
    public void broadcastPlayerList() throws Exception {
        String lobbyCode = lobbyService.createLobby(session1, "Player1");
        lobbyService.joinLobby(session2, lobbyCode, "Player2");

        reset(messagingService);

        lobbyService.broadcastPlayerList(lobbyCode);

        List<String> players = List.of("Player1", "Player2");

        verify(messagingService).sendMessage(eq(session1), eq(messageFactory.lobbyUpdate(players)));
        verify(messagingService).sendMessage(eq(session2), eq(messageFactory.lobbyUpdate(players)));
    }
}
