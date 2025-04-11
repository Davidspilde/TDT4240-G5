package io.github.Spyfall.services.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.message.response.GameSpyCaughtMessage;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.message.response.GameVoteMessage;
import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.websocket.handlers.WebSocketMessageHandler;

public class RecieveMessageService {
    private static RecieveMessageService instance;
    private final ObjectMapper objectMapper;
    private final GameModel gameModel;
    private final Map<String, WebSocketMessageHandler<?>> handlers = new HashMap<>();

    private RecieveMessageService() {
        gameModel = GameModel.getInstance();
        objectMapper = new ObjectMapper();

        Reflections reflections = new Reflections("com.interloperServer.interloperServer.websocket.handlers");

        Set<Class<? extends WebSocketMessageHandler>> handlerClasses = reflections
                .getSubTypesOf(WebSocketMessageHandler.class);

        
        //Creates list of all handlers
        List<WebSocketMessageHandler<?>> handlers = new ArrayList<>();
        try {
            for (Class<? extends WebSocketMessageHandler> handler : handlerClasses) {
                handlers.add(handler.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static RecieveMessageService GetInstance() {
        if (instance == null) {
            instance = new RecieveMessageService();
        }
        return instance;
    }

    public void handleMessage(String msg) {

        try {
            JsonNode json = objectMapper.readTree(msg);

            String event = json.get("event").asText();
            WebSocketMessageHandler<?> handler = handlers.get(event);

            dispatchToHandler(handler, json);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe cast due to controlled getMessageClass()
    private void dispatchToHandler(WebSocketMessageHandler handler, JsonNode json) throws Exception {

        Object message = objectMapper.treeToValue(json, handler.GetMessageClass());

        handler.handle(message);
    }

    // Handlers for each message type
    private void handleGameComplete(GameCompleteMessage msg) {
        System.out.println("Handling game complete: " + msg);

        System.out.println("Game complete received: " + msg.getScoreboard());

        // return to lobby?
        // gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleNewRound(GameNewRoundMessage msg) {
        System.out.println("Handling new round: " + msg);

        System.out.println("New round received: Round " + msg.getRoundNumber());

        // update model with new data

        gameModel.getGameData().setCurrentRound(msg.getRoundNumber());
        gameModel.getGameData().setTimeRemaining(msg.getRoundDuration());

        // bruh
        boolean isSpy = (msg.getRole() != null && msg.getRole().equalsIgnoreCase("spy"));
        gameModel.getGameData().setSpy(isSpy);

        // set location and role
        gameModel.getGameData().setLocation(msg.getLocation());
        gameModel.getGameData().setRole(msg.getRole());

        // potential locations for spy
        if (isSpy) {
            // should fetch from backend
            List<String> defaultLocations = new ArrayList<>(Arrays.asList(
                    "Airplane", "Bank", "Beach", "Casino", "Hospital",
                    "Hotel", "Military Base", "Movie Studio", "Ocean Liner",
                    "Passenger Train", "Restaurant", "School", "Space Station",
                    "Submarine", "Supermarket", "University"));
            gameModel.getGameData().setPossibleLocations(defaultLocations);
        }

        // change game state if not there already
        if (gameModel.getCurrentState() != GameState.IN_GAME) {
            gameModel.setCurrentState(GameState.IN_GAME);
        }
    }

    private void handleRoundEnded(GameRoundEndedMessage msg) {
        System.out.println("Handling round ended: " + msg);

        // TODO:
        // Update scoreboard if needed
        // Wait for the next round to start
    }

    private void handleSpyCaught(GameSpyCaughtMessage msg) {
        System.out.println("Handling spy caught: " + msg);

        // TODO: Show spy
    }

    private void handleSpyGuess(GameSpyGuessMessage msg) {
        System.out.println("Handling spy guess: " + msg);

        // TODO: Show guess
    }

    private void handleVote(GameVoteMessage msg) {
        System.out.println("Handling vote: " + msg);

        // TODO: Show votes? vote counter?
    }

    private void handleLobbyCreated(LobbyCreatedMessage msg) {
        System.out.println("Handling lobby created: " + msg.getLobbyCode());

        // update the model with lobby info
        gameModel.setLobbyCode(msg.getLobbyCode());
        gameModel.getLobbyData().setHostPlayer(msg.getHost());

        // add player to the player list
        gameModel.getLobbyData().getPlayers().clear();
        gameModel.getLobbyData().addPlayer(gameModel.getUsername());

        // transition to lobby state
        gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleLobbyJoined(LobbyJoinedMessage msg) {
        System.out.println("Handling lobby joined: " + msg);

        // update model
        gameModel.setLobbyCode(msg.getLobbyCode());
        gameModel.getLobbyData().setHostPlayer(msg.getHost());

        // transition to lobby state
        gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleLobbyNewHost(LobbyNewHostMessage msg) {
        System.out.println("Handling new lobby host: " + msg);

        // update model
        gameModel.getLobbyData().setHostPlayer(msg.getHost());
    }

    private void handleLobbyPlayers(LobbyPlayersMessage msg) {
        System.out.println("Handling lobby players: " + msg);

        // update the model with player list
        gameModel.getLobbyData().setPlayers(msg.getPlayers());
    }

    private void handleResponse(ResponseMessage msg) {
        System.out.println("Handling generic response: " + msg);
    }
}
