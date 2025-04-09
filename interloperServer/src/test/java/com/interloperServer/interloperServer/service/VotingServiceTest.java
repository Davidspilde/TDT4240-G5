package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

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

    @Mock
    private GameService gameService;

    @Mock
    private RoundService roundService;

    @InjectMocks
    private VotingService votingService;

    private Game game;
    private Lobby lobby;
    private Player p1;
    private Player p2;
    private Player p3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1");
        p2 = new Player(mock(WebSocketSession.class), "Player2");
        p3 = new Player(mock(WebSocketSession.class), "Player3");

        LobbyOptions lobbyOptions = new LobbyOptions(3, 25, 1, 10, 120);
        lobby = new Lobby("abc123", p1, lobbyOptions);
        lobby.addPlayer(p2);
        lobby.addPlayer(p3);

        game = new Game(lobby);
        game.startNextRound();

        when(gameManagerService.getGame("abc123")).thenReturn(game);

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
