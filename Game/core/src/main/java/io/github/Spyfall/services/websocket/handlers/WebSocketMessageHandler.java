package io.github.Spyfall.services.websocket.handlers;

public interface WebSocketMessageHandler<T> {

    String getEvent(); // This needs to be unique

    Class<T> getMessageClass();

    void handle(T message);
}
