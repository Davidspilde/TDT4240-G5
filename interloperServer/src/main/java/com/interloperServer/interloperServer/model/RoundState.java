package com.interloperServer.interloperServer.model;

/**
 * Represents the possible states of a round in the game.
 * <p>
 * This enum defines the different phases a round can be in:
 * <ul>
 * <li>{@code NORMAL} - The round is in progress, and players are actively
 * participating.</li>
 * <li>{@code SPY_LAST_ATTEMPT} - The spy is making their final attempt to guess
 * the location.</li>
 * <li>{@code ENDED} - The round has concluded.</li>
 * </ul>
 */
public enum RoundState {
    NORMAL,
    SPY_LAST_ATTEMPT,
    ENDED
}
