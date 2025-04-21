package com.interloperServer.interloperServer.model.messages.outgoing;

/**
 * Represents an outgoing message sent by the server to notify players about the
 * start of a new round.
 * <p>
 * This message contains the following fields:
 * <ul>
 * <li><b>roundNumber</b>: The number of the current round.</li>
 * <li><b>roundDuration</b>: The duration of the round in seconds.</li>
 * <li><b>role</b>: The role assigned to the player (e.g., "Spy" or a specific
 * role).</li>
 * <li><b>firstQuestioner</b>: The username of the player who will ask the first
 * question.</li>
 * <li><b>location</b>: The location for the round (optional, null if the player
 * is the spy).</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link GameMessage}, which includes the following field:
 * <ul>
 * <li><b>event</b>: The type of the message (e.g., "newRound").</li>
 * </ul>
 */
public class NewRoundMessage extends GameMessage {
	private final int roundNumber;
	private final int roundDuration;
	private final String role;
	private final String firstQuestioner;
	private final String location; // optional (null for spy)

	public NewRoundMessage(int roundNumber, int roundDuration, String role, String firstQuestioner, String location) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.firstQuestioner = firstQuestioner;
		this.location = location;
	}

	public NewRoundMessage(int roundNumber, int roundDuration, String role, String firstQuestioner) {
		super("newRound");
		this.roundNumber = roundNumber;
		this.roundDuration = roundDuration;
		this.role = role;
		this.firstQuestioner = firstQuestioner;
		this.location = null;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public int getRoundDuration() {
		return roundDuration;
	}

	public String getRole() {
		return role;
	}

	public String getLocation() {
		return location;
	}

	public String getFirstQuestioner() {
		return firstQuestioner;
	}
}
