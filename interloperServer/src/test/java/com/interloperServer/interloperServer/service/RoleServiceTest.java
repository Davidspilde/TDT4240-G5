package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleServiceTest {

    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        roleService = new RoleService();
    }

    @Test
    @DisplayName("Assign roles with 3 players: first shuffled becomes SPY, others become PLAYER")
    public void assignRolesTest() {
        Player p1 = new Player(null, "Player1", LobbyRole.PLAYER);
        Player p2 = new Player(null, "Player2", LobbyRole.PLAYER);
        Player p3 = new Player(null, "Player3", LobbyRole.PLAYER);

        List<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
        Game game = new Game("lobby123", players, 3, 30);

        roleService.assignRoles(game);

        // Count spies and players
        int spies = 0;
        int normalPlayers = 0;

        for (Player p : players) {
            if (p.getGameRole() == GameRole.SPY)
                spies++;
            else if (p.getGameRole() == GameRole.PLAYER)
                normalPlayers++;
        }

        assertEquals(1, spies, "One spy");
        assertEquals(2, normalPlayers, "Rest are players");
    }

    @Test
    @DisplayName("Should do nothing if no players in the game")
    public void assignRoles_noPlayers() {
        Game emptyGame = new Game("empty", new ArrayList<>(), 3, 30);

        roleService.assignRoles(emptyGame);

        assertEquals(0, emptyGame.getPlayers().size());
    }
}
