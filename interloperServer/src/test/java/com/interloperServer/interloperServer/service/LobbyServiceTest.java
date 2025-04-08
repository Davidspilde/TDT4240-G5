package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

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

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Creating a new lobby should assign the creator as host")
    public void createLobby_assignsHost() throws Exception {
        String username = "Player1";
        String lobbyCode = lobbyService.createLobby(session1, username);

        assertNotNull(lobbyCode);

        // There is exactly 1 player in the new lobby so the player should be the host
        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);

        assertEquals(1, players.size());
        assertEquals(username, players.get(0).getUsername());
        assertEquals(lobbyService.getLobbyFromLobbyCode(lobbyCode).getHost(), players.get(0));

        // Check that a success message was sent
        verify(messagingService).sendMessage(eq(session1), eq(Map.of(
                "event", "lobbyCreated",
                "lobbyCode", lobbyCode,
                "host", username)));
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

        // Verify the "joinedLobby" message
        verify(messagingService).sendMessage(eq(session2), eq(Map.of(
                "event", "joinedLobby",
                "lobbyCode", lobbyCode,
                "host", "Player1")));

        // Verify the "lobbyUpdate" message
        verify(messagingService, times(2)).sendMessage(eq(session2), any());
        verify(messagingService).sendMessage(eq(players.get(0).getSession()), eq(Map.of(
                "event", "lobbyUpdate",
                "players", List.of("Player1", "Player2"))));
    }

    @Test
    @DisplayName("Joining non-existent lobby should send error message")
    public void joinLobby_invalidCode() throws Exception {
        boolean joined = lobbyService.joinLobby(session2, "fakeCode", "Player2");
        assertFalse(joined);

        verify(messagingService).sendMessage(eq(session2), eq(Map.of(
                "event", "error",
                "message", "Lobby not found!")));
    }

    @Test
    @DisplayName("Removing user should remove them from lobby. If host leaves, new host is assigned.")
    public void removeUserTest() {
        String lobbyCode = lobbyService.createLobby(session1, "Player1");
        lobbyService.joinLobby(session2, lobbyCode, "Player2");

        // Check initial state
        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(2, players.size());

        // remove host (Player1)
        lobbyService.removeUser(session1);

        // Player2 should now be host
        players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(1, players.size());
        assertEquals("Player2", players.get(0).getUsername());
        assertEquals(lobbyService.getLobbyFromLobbyCode(lobbyCode).getHost(), players.get(0));

        // remove Player2 => lobby empty => remove entire lobby
        lobbyService.removeUser(session2);
        players = lobbyService.getPlayersInLobby(lobbyCode);
        assertEquals(0, players.size());
    }

    @Test
    @DisplayName("broadcastPlayerList should send updated list of usernames to all players")
    public void broadcastPlayerList() throws Exception {
        String lobbyCode = lobbyService.createLobby(session1, "Player1");
        lobbyService.joinLobby(session2, lobbyCode, "Player2");

        // reset to only capture broadcast calls
        reset(messagingService);

        lobbyService.broadcastPlayerList(lobbyCode);

        // Capture the messages
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> messageCaptor = ArgumentCaptor.forClass(Map.class);

        verify(messagingService, times(2)).sendMessage(any(), messageCaptor.capture());

        List<Map<String, Object>> allMessages = messageCaptor.getAllValues();

        Map<String, Object> expectedMessage = Map.of(
                "event", "lobbyUpdate",
                "players", List.of("Player1", "Player2"));

        assertTrue(allMessages.contains(expectedMessage), "lobbyUpdate not found in sent messages.");
    }
}
