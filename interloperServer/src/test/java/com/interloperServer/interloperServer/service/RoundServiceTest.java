
package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.model.messages.outgoing.NewRoundMessage;
import com.interloperServer.interloperServer.model.messages.outgoing.RoundEndedMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@DisplayName("RoundService Tests")
class RoundServiceTest {

    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private GameManagerService gameManagerService;
    private RoundService roundService;

    private Game game;
    private Round round;
    private Player spyPlayer;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        gameManagerService = mock(GameManagerService.class);

        roundService = new RoundService(messagingService, messageFactory, gameManagerService);

        spyPlayer = new Player(null, "spy");
        player1 = new Player(null, "p1");
        player2 = new Player(null, "p2");

        game = mock(Game.class);
        round = mock(Round.class);

        when(round.getSpy()).thenReturn(spyPlayer);
        when(round.getRoundNumber()).thenReturn(1);
        when(round.getLocation()).thenReturn("Space Station");

        when(game.getCurrentRound()).thenReturn(round);
        when(game.getPlayers()).thenReturn(List.of(spyPlayer, player1, player2));
        when(game.getLobby()).thenReturn(mock(Lobby.class));
        when(game.getScoreboard()).thenReturn(Map.of("spy", 0, "p1", 0, "p2", 0));
    }

    @Test
    @DisplayName("Should end game and broadcast final message when no more rounds")
    void testAdvanceRound_EndsGameIfNoMoreRounds() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        when(game.hasMoreRounds()).thenReturn(false);

        roundService.advanceRound("ABC");

        verify(round).endRound();
        verify(messageFactory).gameComplete(any());
        verify(messagingService).broadcastMessage(eq(game.getLobby()), any());
    }

    @Test
    @DisplayName("Should start next round and broadcast round start")
    void testAdvanceRound_StartsNextRound() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        when(game.hasMoreRounds()).thenReturn(true);
        when(game.getCurrentRound()).thenReturn(round);
        when(round.getRoundDuration()).thenReturn(90);
        when(round.getRoundNumber()).thenReturn(2);
        when(round.getLocation()).thenReturn("Moon");

        when(messageFactory.newRound(anyInt(), anyInt(), eq("Player"), anyString()))
                .thenReturn(mock(NewRoundMessage.class));
        when(messageFactory.newRound(anyInt(), anyInt(), eq("Spy")))
                .thenReturn(mock(NewRoundMessage.class));

        roundService.advanceRound("ABC");

        verify(game).startNextRound();
        verify(messagingService, atLeastOnce()).sendMessage(any(), any());
    }

    @Test
    @DisplayName("Should award players if spy is caught and end round")
    void testEndRoundDueToVotes_SpyCaught() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);

        RoundEndedMessage mockEndMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), eq(true), anyBoolean(), anyString(), anyString(),
                anyMap()))
                .thenReturn(mockEndMessage);

        roundService.endRoundDueToVotes("ABC", true, "spy");

        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(mockEndMessage));
        verify(game).updateScore("p1", 1);
        verify(game).updateScore("p2", 1);
        verify(game, never()).updateScore(eq("spy"), anyInt());
    }

    @Test
    @DisplayName("Should award spy if guess is correct and end round")
    void testEndRoundDueToSpyGuess_Successful() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);

        RoundEndedMessage mockEndMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), anyBoolean(), eq(true), anyString(), anyString(),
                anyMap()))
                .thenReturn(mockEndMessage);

        roundService.endRoundDueToSpyGuess("ABC", "spy", true);

        verify(game).updateScore("spy", 1);
        verify(game, never()).updateScore("p1", 1);
        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(mockEndMessage));
    }

    @Test
    @DisplayName("Should award spy if timeout and no one guessed or caught spy")
    void testEndRoundDueToTimeout_AwardsSpy() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);

        RoundEndedMessage mockEndMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), eq(false), eq(false), eq("spy"), anyString(), anyMap()))
                .thenReturn(mockEndMessage);

        roundService.endRoundDueToTimeout("ABC", "spy");

        verify(game).updateScore("spy", 1);
        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(mockEndMessage));
    }
}
