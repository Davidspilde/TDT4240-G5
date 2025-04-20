package io.github.Spyfall.services.websocket.handlers;

import io.github.Spyfall.message.response.ErrorMessage;
import io.github.Spyfall.view.ui.ErrorPopup;

public class ErrorHandler implements WebSocketMessageHandler<ErrorMessage> {

    @Override
    public String getEvent() {
        return "error";
    }

    @Override
    public Class<ErrorMessage> getMessageClass() {
        return ErrorMessage.class;
    }

    @Override
    public void handle(ErrorMessage message) {
        ErrorPopup.getInstance().showServerError(message.getEvent(), message.getMessage());

    }

}
