package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.GameRole;
import com.interloperServer.interloperServer.model.Player;

@Service
public class RoleService {
    /**
     * Assigns roles to players in a game.
     */
    public void assignRoles(Game game) {
        Collections.shuffle(players);
        players.get(0).setGameRole(GameRole.SPY);
        players.subList(1, players.size()).forEach(p -> p.setGameRole(GameRole.PLAYER));
    }
}
