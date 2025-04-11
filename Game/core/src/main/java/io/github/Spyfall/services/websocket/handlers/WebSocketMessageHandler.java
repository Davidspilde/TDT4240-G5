package io.github.Spyfall.services.websocket.handlers;

public interface WebSocketMessageHandler<T> {

    String getEvent(); // This needs to be unique

    Class<T> GetMessageClass();

    void handle(T message);
}
