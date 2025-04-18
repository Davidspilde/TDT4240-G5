
package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.model.messages.outgoing.NewRoundMessage;
import com.interloperServer.interloperServer.model.messages.outgoing.RoundEndedMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

        Location location = mock(Location.class);
        when(location.getName()).thenReturn("Space Station");

        when(round.getSpy()).thenReturn(spyPlayer);
        when(round.getRoundNumber()).thenReturn(1);
        when(round.getLocation()).thenReturn(location);

        when(game.getCurrentRound()).thenReturn(round);
        when(game.getPlayers()).thenReturn(List.of(spyPlayer, player1, player2));
        when(game.getLobby()).thenReturn(mock(Lobby.class));
        when(game.getScoreboard()).thenReturn(new HashMap<>(Map.of("spy", 0, "p1", 0, "p2", 0)));
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

        Location location = mock(Location.class);
        when(location.getName()).thenReturn("Moon");
        when(location.getRoles()).thenReturn(Arrays.asList("1", "2", "3"));
        when(round.getLocation()).thenReturn(location);
        when(round.getSpy()).thenReturn(spyPlayer);
        when(round.getRoundDuration()).thenReturn(90);
        when(round.getRoundNumber()).thenReturn(2);

        when(messageFactory.newRound(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(mock(NewRoundMessage.class));
        when(messageFactory.newRound(anyInt(), anyInt(), eq("Spy"), anyString()))
                .thenReturn(mock(NewRoundMessage.class));

        roundService.advanceRound("ABC");

        verify(game).startNextRound();
        verify(messagingService, atLeastOnce()).sendMessage(any(), any());
    }

    @Test
    @DisplayName("Should trigger spy last attempt with timer")
    void testStartSpyLastAttempt() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        when(game.getCurrentRound()).thenReturn(round);

        Lobby lobby = mock(Lobby.class);
        LobbyOptions lobbyOptions = mock(LobbyOptions.class);

        when(game.getLobby()).thenReturn(lobby);
        when(lobby.getLobbyOptions()).thenReturn(lobbyOptions);
        when(lobbyOptions.getSpyLastAttemptTime()).thenReturn(30);

        roundService.startSpyLastAttempt("ABC", "spy");

        verify(round).setSpyLastAttempt();
        verify(messageFactory).spyLastAttempt(eq("spy"), anyInt());
        verify(messagingService).broadcastMessage(eq(game.getLobby()), any());
        verify(game).startTimer(eq(30), any());
    }

    @Test
    @DisplayName("Should award players if spy is caught and guessed wrong")
    void testEndRoundDueToGuess_SpyCaughtAndWrongGuess() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        RoundEndedMessage endMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), eq(true), eq(false), any(), any(), anyMap()))
                .thenReturn(endMessage);

        roundService.endRoundDueToGuess("ABC", "spy", true, false);

        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(endMessage));
        verify(game).updateScore(eq("p1"), eq(1));
        verify(game).updateScore(eq("p2"), eq(1));
        verify(game, never()).updateScore(eq("spy"), anyInt());
    }

    @Test
    @DisplayName("Should award spy if guess is correct")
    void testEndRoundDueToGuess_SpyCorrectGuess() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        RoundEndedMessage endMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), eq(false), eq(true), any(), any(), anyMap()))
                .thenReturn(endMessage);

        roundService.endRoundDueToGuess("ABC", "spy", false, true);

        verify(game).updateScore("spy", 1);
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(endMessage));
    }

    @Test
    @DisplayName("Should award spy if round times out")
    void testEndRoundDueToTimeout() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        RoundEndedMessage endMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(anyInt(), anyString(), eq(false), eq(false), eq("spy"), anyString(), anyMap()))
                .thenReturn(endMessage);

        roundService.endRoundDueToTimeout("ABC", "spy");

        verify(game).updateScore("spy", 1);
        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(endMessage));
    }

    @Test
    @DisplayName("Should end round with no points when spy disconnects")
    void testEndRoundDueToSpyDisconnect() {
        when(gameManagerService.getGame("ABC")).thenReturn(game);
        when(game.getCurrentRound()).thenReturn(round);

        RoundEndedMessage endMessage = mock(RoundEndedMessage.class);
        when(messageFactory.roundEnded(eq(1), eq("SPY_DISCONNECT"), eq(false), eq(false),
                eq("spy"), eq("Space Station"), anyMap()))
                .thenReturn(endMessage);

        roundService.endRoundDueToSpyDisconnect("ABC");

        verify(round).endRound();
        verify(game).stopTimer();
        verify(messagingService).broadcastMessage(eq(game.getLobby()), eq(endMessage));

        // Ensure no points were awarded
        verify(game, never()).updateScore(anyString(), anyInt());
    }

}
