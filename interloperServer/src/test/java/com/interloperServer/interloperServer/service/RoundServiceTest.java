package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RoundServiceTest {

    @Mock
    private MessagingService messagingService;
    @Mock
    private GameManagerService gameManagerService;

    @InjectMocks
    private RoundService roundService;

    private Game game;
    private Lobby lobby;
    private Player p1;
    private Player p2;
    private Player spy;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1");
        p2 = new Player(mock(WebSocketSession.class), "Player2");
        spy = new Player(mock(WebSocketSession.class), "Player3");

        LobbyOptions lobbyOptions = new LobbyOptions(3, 25, 1, 10, 120);
        lobby = new Lobby("abc123", p1, lobbyOptions);
        lobby.addPlayer(p2);
        lobby.addPlayer(spy);

        game = new Game(lobby); // 3 rounds
        when(gameManagerService.getGame("abc123")).thenReturn(game);
    }

    @Test
    @DisplayName("Should end the game if no more rounds remain")
    public void noMoreRounds_shouldEndGame() {
        // Move to the last round
        roundService.advanceRound("abc123");
        roundService.advanceRound("abc123"); // Last round

        roundService.advanceRound("abc123");

        // Verify the "gameComplete" message
        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "gameComplete",
                "scores", game.getScoreboard())));
    }

    @Test
    @DisplayName("Should advance to next round, assign roles, and send correct messages")
    public void advanceRound_startNewRoundAndNotify() {
        roundService.advanceRound("abc123");

        // Verify messages sent to players
        verify(messagingService).sendMessage(eq(p1.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", "Player",
                "location", game.getCurrentRound().getLocation())));
        verify(messagingService).sendMessage(eq(p2.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", "Player",
                "location", game.getCurrentRound().getLocation())));

        // Spy only gets round number and role, not location
        verify(messagingService).sendMessage(eq(spy.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", "Spy")));

    }
}
