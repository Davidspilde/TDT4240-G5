
package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.model.messages.outgoing.GameMessage;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class GameServiceTest {

    private VotingService votingService;
    private RoundService roundService;
    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private GameManagerService gameManagerService;
    private LobbyService lobbyService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        votingService = mock(VotingService.class);
        roundService = mock(RoundService.class);
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        gameManagerService = mock(GameManagerService.class);
        lobbyService = mock(LobbyService.class);

        gameService = new GameService(votingService, roundService, messagingService, messageFactory, gameManagerService,
                lobbyService);
    }

    @Test
    void startGame_ShouldSendError_IfLobbyNotFound() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(lobbyService.getLobbyFromLobbyCode("XYZ")).thenReturn(null);

        gameService.startGame("Alice", "XYZ", session);

        verify(messagingService).sendMessage(eq(session), any());
    }

    @Test
    void startGame_ShouldSendError_IfNotHost() {
        Lobby lobby = mock(Lobby.class);
        WebSocketSession session = mock(WebSocketSession.class);
        when(lobby.getHost()).thenReturn(new Player(null, "Bob"));
        when(lobbyService.getLobbyFromLobbyCode("ABC")).thenReturn(lobby);

        gameService.startGame("Alice", "ABC", session);

        verify(messagingService).sendMessage(eq(session), any());
    }

    @Test
    void startGame_ShouldSendError_IfTooFewPlayers() {
        Lobby lobby = mock(Lobby.class);
        WebSocketSession session = mock(WebSocketSession.class);
        when(lobby.getHost()).thenReturn(new Player(null, "Alice"));
        when(lobby.getPlayers()).thenReturn(List.of(new Player(null, "Alice"))); // only 1 player
        when(lobbyService.getLobbyFromLobbyCode("ABC")).thenReturn(lobby);

        gameService.startGame("Alice", "ABC", session);

        verify(messagingService).sendMessage(eq(session), any());
    }

    @Test
    void startGame_ShouldStartGame_IfValidConditionsMet() {
        Lobby lobby = mock(Lobby.class);
        WebSocketSession session = mock(WebSocketSession.class);

        when(lobby.getHost()).thenReturn(new Player(null, "Alice"));
        when(lobby.getPlayers()).thenReturn(List.of(
                new Player(null, "Alice"),
                new Player(null, "Bob")));
        when(lobby.getLobbyCode()).thenReturn("XYZ");

        // 🛠️ Fix: Add this to prevent NullPointerException
        LobbyOptions options = new LobbyOptions(5, 30, 1, 8, 120);
        when(lobby.getLobbyOptions()).thenReturn(options);

        when(lobbyService.getLobbyFromLobbyCode("XYZ")).thenReturn(lobby);

        GameMessage mockGameMessage = mock(GameMessage.class);
        when(messageFactory.gameStarted()).thenReturn(mockGameMessage);

        gameService.startGame("Alice", "XYZ", session);

        verify(gameManagerService).storeGame(eq("XYZ"), any(Game.class));
        verify(messagingService).broadcastMessage(eq(lobby), eq(mockGameMessage));
        verify(roundService).advanceRound(eq("XYZ"));
    }

    @Test
    void handlePlayerDisconnect_ShouldEndGame_IfTooFewPlayers() {
        Game game = mock(Game.class);
        when(game.getPlayers()).thenReturn(Collections.singletonList(mock(Player.class)));
        when(gameManagerService.getGame("XYZ")).thenReturn(game);

        gameService.handlePlayerDisconnect(mock(WebSocketSession.class), "XYZ");

        verify(gameManagerService).removeGame("XYZ");
        verify(messagingService).broadcastMessage(any(), any());
    }

    @Test
    void castVote_DelegatesToVotingService() {
        Game game = mock(Game.class);
        when(gameManagerService.getGame("XYZ")).thenReturn(game);

        gameService.castVote("XYZ", "voter", "target");

        verify(votingService).castVote("XYZ", "voter", "target");
    }

    @Test
    void castSpyGuess_DelegatesToVotingService() {
        Game game = mock(Game.class);
        when(gameManagerService.getGame("XYZ")).thenReturn(game);

        gameService.castSpyGuess("XYZ", "spy", "location");

        verify(votingService).castSpyGuess("XYZ", "spy", "location");
    }

    @Test
    void advanceRound_ShouldAdvance_IfVotingComplete() {
        Game game = mock(Game.class);
        Round round = mock(Round.class);
        when(gameManagerService.getGame("XYZ")).thenReturn(game);
        when(game.getCurrentRound()).thenReturn(round);
        when(round.isVotingComplete()).thenReturn(true);

        gameService.advanceRound("XYZ");

        verify(roundService).advanceRound("XYZ");
        verify(game).stopTimer();
    }

    @Test
    void endRoundDueToTimeout_DelegatesToRoundService() {
        Game game = mock(Game.class);
        Round round = mock(Round.class);
        Player spy = mock(Player.class);
        when(spy.getUsername()).thenReturn("Spy");
        when(round.getSpy()).thenReturn(spy);
        when(game.getCurrentRound()).thenReturn(round);
        when(gameManagerService.getGame("XYZ")).thenReturn(game);

        gameService.endRoundDueToTimeout("XYZ");

        verify(roundService).endRoundDueToTimeout("XYZ", "Spy");
    }

    @Test
    void endGame_ShouldRemoveGame() {
        Lobby lobby = mock(Lobby.class);
        Game game = mock(Game.class);
        when(game.getLobby()).thenReturn(lobby);
        when(gameManagerService.getGame("XYZ")).thenReturn(game);

        gameService.endGame("XYZ");

        verify(gameManagerService).removeGame("XYZ");
    }
}
