package com.interloperServer.interloperServer.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;
import com.interloperServer.interloperServer.websocket.handlers.WebSocketMessageHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("GameWebSocketHandler Tests")
public class GameWebSocketHandlerTest {

    private WebSocketMessageHandler<TestMessage> mockHandler;
    private WebSocketSession mockSession;
    private MessageDispatcher dispatcher;
    private GameWebSocketHandler handler;
    private MessagingService messagingService;
    private GameMessageFactory messageFactory;
    private GameConnectionService connectionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        mockHandler = mock(WebSocketMessageHandler.class);
        mockSession = mock(WebSocketSession.class);
        messagingService = mock(MessagingService.class);
        messageFactory = mock(GameMessageFactory.class);
        connectionService = mock(GameConnectionService.class);

        when(mockHandler.getType()).thenReturn("testEvent");
        when(mockHandler.getMessageClass()).thenReturn(TestMessage.class);

        dispatcher = new MessageDispatcher(List.of(mockHandler), objectMapper);
        handler = new GameWebSocketHandler(dispatcher, messagingService,
                messageFactory, connectionService);
    }

    @Test
    @DisplayName("Should dispatch known message type to its handler")
    void shouldDispatchMessageToCorrectHandler() throws Exception {
        String json = """
                {
                  "type": "testEvent",
                  "value": "hello"
                }
                """;

        TextMessage message = new TextMessage(json);
        handler.handleTextMessage(mockSession, message);

        ArgumentCaptor<TestMessage> captor = ArgumentCaptor.forClass(TestMessage.class);
        verify(mockHandler).handle(captor.capture(), eq(mockSession));

        TestMessage actual = captor.getValue();
        assert actual.getValue().equals("hello");
    }

    @Test
    @DisplayName("Should return error when message type is unknown")
    void shouldSendUnknownMessageResponseWhenNoHandler() throws Exception {
        String json = """
                {
                  "type": "nonexistent"
                }
                """;

        TextMessage message = new TextMessage(json);
        WebSocketSession session = mock(WebSocketSession.class);

        handler.handleTextMessage(session, message);

        verify(session).sendMessage(argThat((TextMessage m) -> m.getPayload().contains("Unknown message type")));
    }

    // Dummy DTO for testing
    public static class TestMessage {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
