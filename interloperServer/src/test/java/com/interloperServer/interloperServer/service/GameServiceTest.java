package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

class GameServiceTest {

    @Mock
    private VotingService votingService;
    @Mock
    private RoundService roundService;
    @Mock
    private RoleService roleService;
    @Mock
    private MessagingService messagingService;
    @Mock
    private GameManagerService gameManagerService;
    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private Player p1, p2, p3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1", LobbyRole.PLAYER);
        p2 = new Player(mock(WebSocketSession.class), "Player2", LobbyRole.PLAYER);
        p3 = new Player(mock(WebSocketSession.class), "Player3", LobbyRole.HOST);

        List<Player> players = new ArrayList<>(List.of(p1, p2, p3));
        game = new Game("lobby123", players, 2, 10);

        when(gameManagerService.getGame("lobby123")).thenReturn(game);
    }

    @Test
    @DisplayName("Should start game if the caller is the host")
    public void startGame_hostCanStart() {
        when(lobbyService.isHost("lobby123", "Player3")).thenReturn(true);
        when(lobbyService.getPlayersInLobby("lobby123")).thenReturn(game.getPlayers());

        WebSocketSession mockSession = mock(WebSocketSession.class);
        boolean result = gameService.startGame("lobby123", "Player3", lobbyService, mockSession);

        assertTrue(result, "startGame should return true if the host started the game");

        // Grab the Game instance passed to assignRoles()
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(roleService).assignRoles(gameCaptor.capture());

        // Check that its the correct game being started
        Game capturedGame = gameCaptor.getValue();
        assertEquals("lobby123", capturedGame.getLobbyCode());
        assertEquals(3, capturedGame.getPlayers().size());

        verify(gameManagerService).storeGame(eq("lobby123"), eq(capturedGame));
    }

    @Test
    @DisplayName("Should not start game if the caller is not the host")
    public void startGame_nonHostShouldFail() {
        when(lobbyService.isHost("lobby123", "Player1")).thenReturn(false);

        WebSocketSession mockSession = mock(WebSocketSession.class);
        boolean result = gameService.startGame("lobby123", "Player1", lobbyService, mockSession);

        assertFalse(result, "startGame should return false if non-host tries to start");

        verify(messagingService).sendMessage(eq(mockSession), contains("Only the host can start"));
        verifyNoInteractions(roleService);
    }

    @Test
    @DisplayName("Should remove player and end game if nobody remains")
    public void handlePlayerDisconnect_lastPlayerLeaves() {
        // game has 3 players: p1, p2, p3
        WebSocketSession sessionToRemove = p3.getSession();

        // Remove all players but add Player3 back
        game.getPlayers().clear();
        game.getPlayers().add(p3);

        // Remove Player3
        gameService.handlePlayerDisconnect(sessionToRemove, "lobby123");

        // Should remove game from manager
        verify(gameManagerService).removeGame("lobby123");
        verify(messagingService).broadcastMessage(eq(game), contains("Game has ended."));
    }

    @Test
    @DisplayName("Should transfer host role if host disconnects but players remain")
    public void handlePlayerDisconnect_transferHost() {
        // p3 is host, p1 and p2 are players
        WebSocketSession sessionToRemove = p3.getSession();
        gameService.handlePlayerDisconnect(sessionToRemove, "lobby123");

        // Now p3 is removed from game
        assertFalse(game.getPlayers().contains(p3));
        // p1 should become the new host (first in list after removal)
        assertEquals(LobbyRole.HOST, p1.getLobbyRole());
        // Not removing the entire game, since p1, p2 remain
        verify(gameManagerService, never()).removeGame(anyString());
    }

    @Test
    @DisplayName("Should advance round if voting complete, else broadcast not complete")
    public void checkVotingAndAdvance() {
        // Voting incomplete
        gameService.checkVotingAndAdvance("lobby123");

        verify(messagingService).broadcastMessage(eq(game), eq("Voting is not complete yet!"));
        verify(roundService, never()).advanceRound(anyString());

        // Voting complete
        reset(messagingService);
        game.getCurrentRound().setVotingComplete();
        gameService.checkVotingAndAdvance("lobby123");

        verify(roundService).advanceRound("lobby123");
        verify(messagingService, never()).broadcastMessage(eq(game), eq("Voting is not complete yet!"));
    }

    @Test
    @DisplayName("Should end the round, evaluate votes, and broadcast spy info")
    public void beginEndOfRoundTest() {
        gameService.beginEndOfRound("lobby123");

        assertTrue(game.getCurrentRound().isVotingComplete());
        verify(votingService).evaluateVotes("lobby123");
        verify(messagingService).broadcastMessage(eq(game), contains("End of round. Spy was: "));
    }

    @Test
    @DisplayName("Should remove game and announce end when endGame is called")
    public void endGameTest() {
        gameService.endGame("lobby123");
        verify(gameManagerService).removeGame("lobby123");
        verify(messagingService).broadcastMessage(eq(game), contains("Game has ended."));
    }
}
