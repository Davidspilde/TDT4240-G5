package io.github.Spyfall.handlers;

import io.github.Spyfall.message.response.*;

/**
 * Interface for handling different types of response messages
 */
public interface MessageHandler {
    void handleMessage(ResponseMessage message);
}