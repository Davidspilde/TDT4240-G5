package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.LobbyRole;
import com.interloperServer.interloperServer.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals(LobbyRole.HOST, players.get(0).getLobbyRole());

        // Check that a success message was sent
        verify(messagingService).sendMessage(eq(session1), messageCaptor.capture());

        String content = messageCaptor.getValue();
        assertTrue(content.contains("Lobby Created! Code: " + lobbyCode + " (Host: " + username + ")"));
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
        assertEquals(LobbyRole.PLAYER, players.get(1).getLobbyRole());

        // Capture all messages sent to session2
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(messagingService, times(2)).sendMessage(eq(session2), messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();

        // Check that one of the messages was the "joined lobby" message
        assertTrue(messages.stream().anyMatch(msg -> msg.equals("Joined Lobby: " + lobbyCode + ". Host: Player1")));

        // Check that one contains "Lobby Update"
        assertTrue(messages.stream().anyMatch(msg -> msg.contains("Lobby Update")));
    }

    @Test
    @DisplayName("Joining non-existent lobby should send error message")
    public void joinLobby_invalidCode() throws Exception {
        boolean joined = lobbyService.joinLobby(session2, "fakeCode", "Player2");
        assertFalse(joined);

        verify(messagingService).sendMessage(eq(session2), messageCaptor.capture());
        String content = messageCaptor.getValue();
        assertTrue(content.contains("Lobby Not Found!"));
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
        assertEquals(LobbyRole.HOST, players.get(0).getLobbyRole());

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

        reset(messagingService); // reset to only capture broadcast calls

        lobbyService.broadcastPlayerList(lobbyCode);

        // Capture all messages sent by messagingService
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // Expect each player to receive one message
        verify(messagingService).sendMessage(eq(session1), messageCaptor.capture());
        verify(messagingService).sendMessage(eq(session2), messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();

        // Assert that both messages contain "Lobby Update:"
        for (String msg : messages) {
            assertTrue(msg.contains("Lobby Update:"), "Message should contain 'Lobby Update:' but was: " + msg);
        }
    }
}
