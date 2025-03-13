package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import com.interloperServer.interloperServer.model.*;

import java.util.*;

/**
 * Class for handling game related logic
 */
@Service
public class GameService {
    /**
     * Assigns roles (one Spy, rest Players) to all players in the lobby.
     * Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
     * @return True if the host called the method, false if someone else did
     */
    public boolean startGame(String lobbyCode, String username, LobbyService lobbyService) {
        if (!lobbyService.isHost(lobbyCode, username)) {
            return false; // Only host can start
        }

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);
        Collections.shuffle(players);
        players.get(0).setGameRole(GameRole.SPY); // First player is Spy
        players.subList(1, players.size()).forEach(p -> p.setGameRole(GameRole.PLAYER));

        return true;
    }
}
