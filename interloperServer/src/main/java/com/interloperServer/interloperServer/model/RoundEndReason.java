package com.interloperServer.interloperServer.model;

/**
 * Represents the possible reasons for a round ending in the game.
 * <p>
 * This enum defines the various scenarios that can cause a round to end, such
 * as:
 * <ul>
 * <li>{@code VOTES} - The round ended due to a majority vote.</li>
 * <li>{@code SPY_GUESS} - The spy guessed the location.</li>
 * <li>{@code WRONG_VOTE} - The majority vote was incorrect.</li>
 * <li>{@code TIMEOUT} - The round ended because the timer expired.</li>
 * <li>{@code SPY_DISCONNECT} - The round ended because the spy
 * disconnected.</li>
 * </ul>
 */
public enum RoundEndReason {
    VOTES,
    SPY_GUESS,
    WRONG_VOTE,
    TIMEOUT,
    SPY_DISCONNECT
}
