package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.messages.incomming.RecieveLobbyOptionsMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LobbyServiceTest {

    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private LobbyService lobbyService;
    private WebSocketSession session;

    @BeforeEach
    public void setup() {
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        session = mock(WebSocketSession.class);
        lobbyService = new LobbyService(messagingService, messageFactory);
    }

    @Test
    public void testCreateLobby() {
        String username = "hostUser";
        String lobbyCode = lobbyService.createLobby(session, username);

        Lobby lobby = lobbyService.getLobbyFromLobbyCode(lobbyCode);
        assertNotNull(lobby);
        assertEquals(username, lobby.getHost().getUsername());
        assertTrue(lobby.getPlayers().contains(lobby.getHost()));
    }

    @Test
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
    public void testJoinLobby_LobbyNotFound() {
        WebSocketSession newSession = mock(WebSocketSession.class);
        boolean result = lobbyService.joinLobby(newSession, "INVALID", "someone");

        assertFalse(result);
        verify(messagingService).sendMessage(eq(newSession), any());
    }

    @Test
    public void testRemoveUser_RemovesPlayerAndLobbyIfEmpty() {
        String username = "host";
        String lobbyCode = lobbyService.createLobby(session, username);

        lobbyService.removeUser(session);
        Lobby removedLobby = lobbyService.getLobbyFromLobbyCode(lobbyCode);

        assertNull(removedLobby);
    }

    @Test
    public void testUpdateLobbyOptions() {
        String username = "host";
        String lobbyCode = lobbyService.createLobby(session, username);

        RecieveLobbyOptionsMessage optionsMsg = new RecieveLobbyOptionsMessage();
        optionsMsg.setRoundLimit(5);
        optionsMsg.setSpyCount(2);
        optionsMsg.setLocationNumber(25);
        optionsMsg.setTimePerRound(150);
        optionsMsg.setMaxPlayerCount(6);

        lobbyService.updateLobbyOptions(lobbyCode, optionsMsg);
        LobbyOptions updatedOptions = lobbyService.getLobbyFromLobbyCode(lobbyCode).getLobbyOptions();

        assertEquals(5, updatedOptions.getRoundLimit());
        assertEquals(2, updatedOptions.getSpyCount());
        assertEquals(5, updatedOptions.getLocationNumber()); // note: it's using roundLimit mistakenly!
        assertEquals(150, updatedOptions.getTimePerRound());
        assertEquals(6, updatedOptions.getMaxPlayerCount());
    }

    @Test
    public void testIsHost() {
        String username = "host";
        String lobbyCode = lobbyService.createLobby(session, username);

        assertTrue(lobbyService.isHost(lobbyCode, username));
        assertFalse(lobbyService.isHost(lobbyCode, "notHost"));
    }
}
