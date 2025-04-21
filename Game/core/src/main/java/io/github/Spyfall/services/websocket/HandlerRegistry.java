package io.github.Spyfall.services.websocket;

import io.github.Spyfall.services.websocket.handlers.ErrorHandler;
import io.github.Spyfall.services.websocket.handlers.GameHandlers.*;
import io.github.Spyfall.services.websocket.handlers.lobbyHandlers.*;

//Registers all handlers to the dispatcher
public class HandlerRegistry {
    public static void registerAll(MessageDispatcher dispatcher) {
        dispatcher.register(new ErrorHandler());
        dispatcher.register(new GameCompleteHandler());
        dispatcher.register(new GameEndedHandler());
        dispatcher.register(new GameNewRoundHandler());
        dispatcher.register(new GameRoundEndedHandler());
        dispatcher.register(new GameSpyGuessHandler());
        dispatcher.register(new GameSpyLastAttemptHandler());
        dispatcher.register(new GameStartedHandler());
        dispatcher.register(new GameVotedHandler());

        dispatcher.register(new LobbyCreatedHandler());
        dispatcher.register(new LobbyJoinedHandler());
        dispatcher.register(new LobbyLocationsUpdateHandler());
        dispatcher.register(new LobbyNewHostHandler());
        dispatcher.register(new LobbyUpdateHandler());
    }
}
