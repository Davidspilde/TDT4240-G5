
package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;
import com.interloperServer.interloperServer.model.messages.outgoing.GameMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static org.mockito.Mockito.*;

@DisplayName("VotingService Tests")
class VotingServiceTest {

    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private GameManagerService gameManagerService;
    private RoundService roundService;
    private VotingService votingService;

    private Game game;
    private Round round;
    private Player voter;
    private Player target;
    private Player spy;

    @BeforeEach
    void setup() {
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        gameManagerService = mock(GameManagerService.class);
        roundService = mock(RoundService.class);

        votingService = new VotingService(messagingService, messageFactory, gameManagerService, roundService);

        game = mock(Game.class);
        round = mock(Round.class);

        WebSocketSession voterSession = mock(WebSocketSession.class);
        WebSocketSession targetSession = mock(WebSocketSession.class);
        WebSocketSession spySession = mock(WebSocketSession.class);

        voter = new Player(voterSession, "voter");
        target = new Player(targetSession, "target");
        spy = new Player(spySession, "spy");

        when(game.getPlayer(anyString())).thenAnswer(inv -> {
            String username = inv.getArgument(0);
            return switch (username) {
                case "voter" -> voter;
                case "target" -> target;
                case "spy" -> spy;
                default -> null;
            };
        });

        when(game.getPlayers()).thenReturn(List.of(voter, target, spy));
        when(game.getCurrentRound()).thenReturn(round);
        when(gameManagerService.getGame("LOBBY")).thenReturn(game);
        when(round.getVotes()).thenReturn(new HashMap<>());
        when(round.getSpy()).thenReturn(spy);
        when(round.getLocation()).thenReturn("Airport");
    }

    @Test
    @DisplayName("Should cast valid vote and evaluate round")
    void testValidVote_CastsVoteAndEvaluates() {
        GameMessage msg = mock(GameMessage.class);

        when(round.isVotingComplete()).thenReturn(false);
        when(messageFactory.voted()).thenReturn(msg);

        Player p1 = new Player(mock(WebSocketSession.class), "p1");
        Player p2 = new Player(mock(WebSocketSession.class), "p2");
        Player target = new Player(mock(WebSocketSession.class), "target");

        when(game.getPlayer("p1")).thenReturn(p1);
        when(game.getPlayer("target")).thenReturn(target);
        when(game.getPlayers()).thenReturn(List.of(p1, p2, target));
        when(round.getSpy()).thenReturn(target);

        Map<String, String> votes = new HashMap<>();
        votes.put("p1", "target");
        votes.put("p2", "target");
        when(round.getVotes()).thenReturn(votes);

        votingService.castVote("LOBBY", "p1", "target");

        verify(round).castVote("p1", "target");
        verify(messagingService).sendMessage(eq(p1.getSession()), any());
        verify(roundService).endRoundDueToVotes("LOBBY", true, "target");
    }

    @Test
    @DisplayName("Should reject vote if target is not found")
    void testInvalidVote_TargetNotFound() {
        when(round.isVotingComplete()).thenReturn(false);

        votingService.castVote("LOBBY", "voter", "ghost");

        verify(messagingService).sendMessage(eq(voter.getSession()), any());
        verify(round, never()).castVote(any(), any());
    }

    @Test
    @DisplayName("Should reject self-vote but still register it")
    void testInvalidVote_SelfVote() {
        when(round.isVotingComplete()).thenReturn(false);

        votingService.castVote("LOBBY", "voter", "voter");

        verify(messagingService).sendMessage(eq(voter.getSession()), anyString());
        verify(round).castVote(eq("voter"), eq("voter"));
    }

    @Test
    @DisplayName("Should do nothing if voter is not in game")
    void testInvalidVote_VoterNotInGame() {
        when(round.isVotingComplete()).thenReturn(false);
        when(game.getPlayer("ghostVoter")).thenReturn(null);

        votingService.castVote("LOBBY", "ghostVoter", "target");

        verify(messagingService, never()).sendMessage(any(), any());
        verify(round, never()).castVote(any(), any());
    }

    @Test
    @DisplayName("Should end round when majority is reached and spy is caught")
    void testEvaluateVotes_WithMajority_SpyCaught() {
        when(round.getVotes()).thenReturn(Map.of("p1", "spy", "p2", "spy"));
        when(round.isVotingComplete()).thenReturn(false);

        Player p1 = new Player(mock(WebSocketSession.class), "p1");
        Player p2 = new Player(mock(WebSocketSession.class), "p2");

        when(game.getPlayers()).thenReturn(List.of(p1, p2, spy));
        when(round.getSpy()).thenReturn(spy);

        votingService.evaluateVotes("LOBBY");

        verify(roundService).endRoundDueToVotes("LOBBY", true, "spy");
    }

    @Test
    @DisplayName("Should not end round if no majority is reached")
    void testEvaluateVotes_NoMajority_DoesNothing() {
        when(round.getVotes()).thenReturn(Map.of("voter", "target"));
        when(game.getPlayers()).thenReturn(List.of(voter, target, spy));
        when(round.isVotingComplete()).thenReturn(false);

        votingService.evaluateVotes("LOBBY");

        verify(roundService, never()).endRoundDueToVotes(any(), anyBoolean(), any());
    }

    @Test
    @DisplayName("Should end round with spy point if guess is correct")
    void testSpyGuess_CorrectGuess() {
        when(round.isVotingComplete()).thenReturn(false);
        when(round.getSpy()).thenReturn(spy);
        when(round.getLocation()).thenReturn("Airport");

        votingService.castSpyGuess("LOBBY", "spy", "Airport");

        verify(roundService).endRoundDueToSpyGuess("LOBBY", "spy", true);
    }

    @Test
    @DisplayName("Should end round with no point if guess is incorrect")
    void testSpyGuess_IncorrectGuess() {
        when(round.isVotingComplete()).thenReturn(false);
        when(round.getSpy()).thenReturn(spy);
        when(round.getLocation()).thenReturn("Airport");

        votingService.castSpyGuess("LOBBY", "spy", "Library");

        verify(roundService).endRoundDueToSpyGuess("LOBBY", "spy", false);
    }

    @Test
    @DisplayName("Should do nothing if non-spy tries to guess")
    void testSpyGuess_NotSpy_NoEffect() {
        when(round.isVotingComplete()).thenReturn(false);
        when(round.getSpy()).thenReturn(spy);

        votingService.castSpyGuess("LOBBY", "voter", "Airport");

        verify(roundService, never()).endRoundDueToSpyGuess(any(), any(), anyBoolean());
    }
}
