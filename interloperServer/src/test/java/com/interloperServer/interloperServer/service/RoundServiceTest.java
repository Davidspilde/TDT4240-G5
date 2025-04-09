package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        LobbyOptions lobbyOptions = new LobbyOptions(2, 25, 1, 10, 120);
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
        verify(messagingService).broadcastMessage(eq(lobby), eq(Map.of(
                "event", "gameComplete",
                "scoreboard", game.getScoreboard())));

    }

    @Test
    @DisplayName("Should advance to next round, assign roles, and send correct messages")
    public void advanceRound_startNewRoundAndNotify() {
        roundService.advanceRound("abc123");

        // Verify messages sent to players
        Round currentRound = game.getCurrentRound();
        Player spy = currentRound.getSpy();
        String location = currentRound.getLocation();
        int roundDuration = game.getRoundDuration();

        for (Player player : game.getPlayers()) {
            if (player.equals(spy)) {
                verify(messagingService).sendMessage(eq(player.getSession()), eq(Map.of(
                        "event", "newRound",
                        "roundNumber", 1,
                        "role", "Spy",
                        "roundDuration", roundDuration)));
            } else {
                verify(messagingService).sendMessage(eq(player.getSession()), eq(Map.of(
                        "event", "newRound",
                        "roundNumber", 1,
                        "role", "Player",
                        "location", location,
                        "roundDuration", roundDuration)));
            }
        }
    }

    @Test
    @DisplayName("Should award all players except the spy when the spy is caught")
    public void spyCaught_awardsOtherPlayers() {
        game.startNextRound();
        game.getCurrentRound().setSpy(spy);

        roundService.endRoundDueToVotes("abc123", true, "Player3");

        assertEquals(1, game.getScoreboard().get("Player1"));
        assertEquals(1, game.getScoreboard().get("Player2"));
        assertEquals(0, game.getScoreboard().get("Player3"));
    }

    @Test
    @DisplayName("Should award spy when they are not caught")
    public void spyNotCaught_awardsSpy() {
        game.startNextRound();
        game.getCurrentRound().setSpy(spy);

        roundService.endRoundDueToVotes("abc123", false, "Player3");

        assertEquals(0, game.getScoreboard().get("Player1"));
        assertEquals(0, game.getScoreboard().get("Player2"));
        assertEquals(1, game.getScoreboard().get("Player3"));
    }

}
