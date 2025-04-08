package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private VotingService votingService;
    @Mock
    private RoundService roundService;
    @Mock
    private MessagingService messagingService;
    @Mock
    private GameManagerService gameManagerService;
    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private Lobby lobby;
    private Player p1, p2, p3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        p1 = new Player(mock(WebSocketSession.class), "Player1");
        p2 = new Player(mock(WebSocketSession.class), "Player2");
        p3 = new Player(mock(WebSocketSession.class), "Player3");

        LobbyOptions options = new LobbyOptions(8, 8, 8, 8, 8);
        lobby = new Lobby("lobby123", p3, options); // p3 is host
        lobby.addPlayer(p2);
        lobby.addPlayer(p1);

        game = new Game(lobby);

        when(lobbyService.getLobbyFromLobbyCode("lobby123")).thenReturn(lobby);
        when(gameManagerService.getGame("lobby123")).thenReturn(game);
    }

    @Test
    @DisplayName("Should start game if the caller is the host")
    public void startGame_hostCanStart() {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        boolean result = gameService.startGame("Player3", "lobby123", mockSession);

        assertTrue(result, "startGame should return true if the host started the game");

        // Capture the Game instance passed to storeGame
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameManagerService).storeGame(eq("lobby123"), gameCaptor.capture());

        Game capturedGame = gameCaptor.getValue();
        assertEquals("lobby123", capturedGame.getLobby().getLobbyCode());
        assertEquals(3, capturedGame.getPlayers().size());
    }

    @Test
    @DisplayName("Should not start game if the caller is not the host")
    public void startGame_nonHostShouldFail() {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        boolean result = gameService.startGame("Player1", "lobby123", mockSession); // Player1 is not host

        assertFalse(result, "startGame should return false if non-host tries to start");

        verify(messagingService).sendMessage(eq(mockSession), eq(Map.of(
                "event", "error",
                "message", "Only the host can start the game.")));
    }

    @Test
    @DisplayName("Should remove player and end game if nobody remains")
    public void handlePlayerDisconnect_lastPlayerLeaves() {
        WebSocketSession sessionToRemove = p3.getSession();

        lobby.removePlayer(p2);
        lobby.removePlayer(p1);
        gameService.handlePlayerDisconnect(sessionToRemove, "lobby123");

        verify(gameManagerService).removeGame("lobby123");
        verify(messagingService).broadcastMessage(eq(lobby), eq(Map.of(
                "event", "gameEnded",
                "message", "Game has ended.")));
    }

    @Test
    @DisplayName("Should end the round, evaluate votes, and broadcast spy info")
    public void beginEndOfRoundTest() {
        gameService.beginEndOfRound("lobby123");

        assertTrue(game.getCurrentRound().isVotingComplete());
        verify(votingService).evaluateVotes("lobby123");
        verify(messagingService).broadcastMessage(eq(lobby), eq(Map.of(
                "event", "roundEnded",
                "spy", game.getCurrentRound().getSpy().getUsername())));
    }

    @Test
    @DisplayName("Should remove game and announce end when endGame is called")
    public void endGameTest() {
        gameService.endGame("lobby123");

        verify(gameManagerService).removeGame("lobby123");
        verify(messagingService).broadcastMessage(eq(lobby), eq(Map.of(
                "event", "gameEnded",
                "message", "Game has ended.")));
    }
}
