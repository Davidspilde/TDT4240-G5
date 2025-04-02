package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VotingServiceTest {

    @Mock
    private MessagingService messagingService;

    @Mock
    private GameManagerService gameManagerService;

    @InjectMocks
    private VotingService votingService;

    private Game game;
    private Player p1;
    private Player p2;
    private Player p3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1", LobbyRole.PLAYER);
        p2 = new Player(mock(WebSocketSession.class), "Player2", LobbyRole.PLAYER);
        p3 = new Player(mock(WebSocketSession.class), "Player3", LobbyRole.PLAYER);

        p1.setGameRole(GameRole.PLAYER);
        p2.setGameRole(GameRole.PLAYER);
        p3.setGameRole(GameRole.SPY);

        List<Player> players = List.of(p1, p2, p3);
        game = new Game("abc123", new ArrayList<>(players), 5, 10);
        when(gameManagerService.getGame("abc123")).thenReturn(game);
    }

    @Test
    @DisplayName("Should catch the spy and award players when majority vote is correct")
    public void spyCaught_awardPlayers() {
        // Player1 and Player2 vote for the spy: Player3
        votingService.castVote("abc123", "Player1", "Player3");
        votingService.castVote("abc123", "Player2", "Player3");

        // Should result in a majority and spy caught
        assertEquals(1, game.getScoreboard().get("Player1"));
        assertEquals(1, game.getScoreboard().get("Player2"));
        assertEquals(0, game.getScoreboard().get("Player3")); // Spy caught = no point
        assertTrue(game.getCurrentRound().isVotingComplete());

        // Verify spyCaught message
        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "spyCaught",
                "spy", "Player3",
                "votes", 2)));

        // Verify spyReveal message
        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "spyReveal",
                "spy", "Player3")));

        // Verify scoreboard message
        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "scoreboard",
                "scores", game.getScoreboard())));
    }

    @Test
    @DisplayName("Should award spy when players vote incorrectly")
    public void spyNotCaught_awardSpy() {
        // Everyone votes for someone who isn't the spy
        votingService.castVote("abc123", "Player1", "Player2");
        votingService.castVote("abc123", "Player2", "Player1");

        // Manually trigger round end
        game.getCurrentRound().setVotingComplete();
        votingService.evaluateVotes("abc123");

        // Should not result in a majority for the spy
        assertEquals(0, game.getScoreboard().get("Player1"));
        assertEquals(0, game.getScoreboard().get("Player2"));
        assertEquals(1, game.getScoreboard().get("Player3")); // Spy should get a point

        // Verify the "spyNotCaught" message
        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "spyNotCaught")));

        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "spyReveal",
                "spy", "Player3")));

        verify(messagingService).broadcastMessage(eq(game), eq(Map.of(
                "event", "scoreboard",
                "scores", game.getScoreboard())));

    }

    @Test
    @DisplayName("Should penalize non-voters when round ends")
    public void nonVoter_penalized() {
        // Only Player1 votes
        votingService.castVote("abc123", "Player1", "Player3");

        // Manually trigger round end
        game.getCurrentRound().setVotingComplete();
        votingService.evaluateVotes("abc123");

        // Player2 didn’t vote and should lose a point
        assertEquals(-1, game.getScoreboard().get("Player2"));

        // Player3 is the spy and should get a point for not having a majority vote
        // against them
        assertEquals(1, game.getScoreboard().get("Player3"));

        verify(messagingService).sendMessage(eq(p2.getSession()), eq(Map.of("event", "notVoted")));

    }

    @Test
    @DisplayName("Should ignore votes after round is complete")
    public void voteAfterRoundComplete_ignored() {
        // Manually trigger round end
        game.getCurrentRound().setVotingComplete();

        // Try to cast vote after game end
        votingService.castVote("abc123", "Player1", "Player3");

        assertTrue(game.getCurrentRound().getVotes().isEmpty()); // No votes should be registered

        // No vote confimation should be sent
        verify(messagingService, never()).sendMessage(any(), eq(Map.of(
                "event", "voted",
                "voter", "Player1")));

    }

    @Test
    @DisplayName("Should not allow voting for a nonexistent player")
    public void voteForInvalidTarget_notAllowed() {
        // Try to vote for a nonexistent user
        votingService.castVote("abc123", "Player1", "Player42");

        assertTrue(game.getCurrentRound().getVotes().isEmpty());

        verify(messagingService).sendMessage(eq(p1.getSession()), argThat(msg -> msg instanceof Map &&
                "invalidVote".equals(((Map<?, ?>) msg).get("event")) &&
                ((Map<?, ?>) msg).get("message").toString().contains("Invalid vote")));

    }

    @Test
    @DisplayName("Should ignore votes from unknown player")
    public void unknownPlayerVote_ignored() {
        votingService.castVote("abc123", "Player0", "Player1");

        assertTrue(game.getCurrentRound().getVotes().isEmpty());
        verifyNoInteractions(messagingService);
    }

    @Test
    @DisplayName("Should not end round if there's a tie")
    public void tieVotes_doesNotEndRound() {
        votingService.castVote("abc123", "Player1", "Player2");
        votingService.castVote("abc123", "Player2", "Player3");

        assertFalse(game.getCurrentRound().isVotingComplete());
        assertEquals(0, game.getScoreboard().get("Player3"));
    }

    @Test
    @DisplayName("Should overwrite previous vote when player votes again")
    public void doubleVote_overwritesPrevious() {
        votingService.castVote("abc123", "Player1", "Player2");
        votingService.castVote("abc123", "Player1", "Player3");

        Map<String, String> votes = game.getCurrentRound().getVotes();
        assertEquals(1, votes.size());
        assertEquals("Player3", votes.get("Player1"));
    }

}
