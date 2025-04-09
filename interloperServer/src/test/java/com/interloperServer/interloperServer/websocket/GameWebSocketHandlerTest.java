package com.interloperServer.interloperServer.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.websocket.handlers.WebSocketMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class GameWebSocketHandlerTest {

    private WebSocketMessageHandler<TestMessage> mockHandler;
    private WebSocketSession mockSession;
    private MessageDispatcher dispatcher;
    private GameWebSocketHandler handler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockHandler = mock(WebSocketMessageHandler.class);
        mockSession = mock(WebSocketSession.class);

        when(mockHandler.getType()).thenReturn("testEvent");
        when(mockHandler.getMessageClass()).thenReturn(TestMessage.class);

        dispatcher = new MessageDispatcher(List.of(mockHandler), objectMapper);
        handler = new GameWebSocketHandler(dispatcher);
    }

    @Test
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
