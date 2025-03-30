package com.interloperServer.interloperServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.messages.CreateLobbyMessage;
import com.interloperServer.interloperServer.model.messages.Message;
import com.interloperServer.interloperServer.model.messages.VoteMessage;
import com.interloperServer.interloperServer.service.GameManagerService;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameWebSocketHandlerTest {

    @Mock
    private GameService gameService;

    @Mock
    private LobbyService lobbyService;

    @Mock
    private GameManagerService gameManagerService;

    @InjectMocks
    private GameWebSocketHandler handler;

    @Mock
    private WebSocketSession session;

    @Captor
    private ArgumentCaptor<TextMessage> textMessageCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create lobby when message type is 'createLobby'")
    void handle_createLobby() throws Exception {
        // Create message
        CreateLobbyMessage createMsg = new CreateLobbyMessage();
        createMsg.setType("createLobby");
        createMsg.setUsername("Alice");

        String json = objectMapper.writeValueAsString(createMsg);

        // Send message
        handler.handleTextMessage(session, new TextMessage(json));

        verify(lobbyService).createLobby(eq(session), eq("Alice"));
    }

    @Test
    @DisplayName("Should join lobby when message type is 'joinLobby'")
    void handle_joinLobby() throws Exception {
        // Create message
        Message joinMsg = new Message();
        joinMsg.setType("joinLobby");
        joinMsg.setLobbyCode("abc123");
        joinMsg.setUsername("Bob");

        String json = objectMapper.writeValueAsString(joinMsg);

        // Send message
        handler.handleTextMessage(session, new TextMessage(json));

        verify(lobbyService).joinLobby(eq(session), eq("abc123"), eq("Bob"));
    }

    @Test
    @DisplayName("Should start game when message type is 'startGame'")
    void handle_startGame() throws Exception {
        // Create message
        Message startMsg = new Message();
        startMsg.setType("startGame");
        startMsg.setLobbyCode("abc123");
        startMsg.setUsername("Alice");

        String json = objectMapper.writeValueAsString(startMsg);

        // Send message
        handler.handleTextMessage(session, new TextMessage(json));

        verify(gameService).startGame(eq("abc123"), eq("Alice"), eq(lobbyService), eq(session));
    }

    @Test
    @DisplayName("Should cast vote when message type is 'vote'")
    void handle_vote() throws Exception {
        // Create message
        VoteMessage voteMsg = new VoteMessage();
        voteMsg.setType("vote");
        voteMsg.setLobbyCode("abc123");
        voteMsg.setUsername("Bob");
        voteMsg.setTarget("Charlie");

        String json = objectMapper.writeValueAsString(voteMsg);

        // Send message
        handler.handleTextMessage(session, new TextMessage(json));

        verify(gameService).castVote("abc123", "Bob", "Charlie");
    }

    @Test
    @DisplayName("Should advance round when message type is 'advanceRound'")
    void handle_advanceRound() throws Exception {
        // Create message
        Message advMsg = new Message();
        advMsg.setType("advanceRound");
        advMsg.setLobbyCode("abc123");
        advMsg.setUsername("Alice");

        String json = objectMapper.writeValueAsString(advMsg);

        // Send message
        handler.handleTextMessage(session, new TextMessage(json));

        verify(gameService).advanceRound("abc123");
    }

    @Test
    @DisplayName("Should send unknown message if type is not valid")
    void handle_unknownMessage() throws Exception {
        String unknownMessage = """
                {
                  "type": "notReal"
                }
                """;

        // Send message
        handler.handleTextMessage(session, new TextMessage(unknownMessage));

        // Should send a message to the session
        verify(session).sendMessage(textMessageCaptor.capture());
        String payload = textMessageCaptor.getValue().getPayload();
        assertTrue(payload.contains("Unknown message type: notReal"));
    }
}
