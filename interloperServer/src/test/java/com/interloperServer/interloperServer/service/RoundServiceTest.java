package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RoundServiceTest {

    @Mock
    private MessagingService messagingService;
    @Mock
    private RoleService roleService;
    @Mock
    private GameManagerService gameManagerService;

    @InjectMocks
    private RoundService roundService;

    private Game game;
    private Player p1;
    private Player p2;
    private Player spy;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1", LobbyRole.PLAYER);
        p2 = new Player(mock(WebSocketSession.class), "Player2", LobbyRole.PLAYER);
        spy = new Player(mock(WebSocketSession.class), "Player3", LobbyRole.PLAYER);

        p1.setGameRole(GameRole.PLAYER);
        p2.setGameRole(GameRole.PLAYER);
        spy.setGameRole(GameRole.SPY);

        List<Player> players = List.of(p1, p2, spy);
        game = new Game("abc123", new ArrayList<>(players), 3, 30); // 3 rounds

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

        // Verify roles are reassigned
        verify(roleService).assignRoles(game);

        // Verify messages sent to players
        verify(messagingService).sendMessage(eq(p1.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", GameRole.PLAYER.toString(),
                "location", game.getCurrentRound().getLocation())));
        verify(messagingService).sendMessage(eq(p2.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", GameRole.PLAYER.toString(),
                "location", game.getCurrentRound().getLocation())));

        // Spy only gets round number and role, not location
        verify(messagingService).sendMessage(eq(spy.getSession()), eq(Map.of(
                "event", "newRound",
                "roundNumber", 2,
                "role", GameRole.SPY.toString())));

    }
}
