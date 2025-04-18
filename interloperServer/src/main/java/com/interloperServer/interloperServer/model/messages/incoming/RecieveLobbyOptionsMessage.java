package com.interloperServer.interloperServer.model.messages.incoming;

/**
 * Represents an incoming message for updating the lobby settings.
 * <p>
 * The message contains the following fields:
 * <ul>
 * <li><b>roundLimit</b>: The number of rounds in the game.</li>
 * <li><b>locationNumber</b>: The number of locations available in the
 * game.</li>
 * <li><b>maxPlayerCount</b>: The maximum number of players allowed in the
 * lobby.</li>
 * <li><b>timePerRound</b>: The duration of each round in seconds.</li>
 * <li><b>spyLastAttemptTime</b>: The time allocated for the spy's last attempt
 * in seconds.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link RecieveMessage}, which includes the following
 * fields:
 * <ul>
 * <li><b>type</b>: The type of the message (e.g., "updateOptions").</li>
 * <li><b>lobbyCode</b>: The code of the lobby being updated.</li>
 * <li><b>username</b>: The username of the player sending the message.</li>
 * </ul>
 */
public class RecieveLobbyOptionsMessage extends RecieveMessage {
    private int roundLimit;
    private int locationNumber;
    private int maxPlayerCount;
    private int timePerRound;
    private int spyLastAttemptTime;

    public RecieveLobbyOptionsMessage() {
    }

    public int getSpyLastAttemptTime() {
        return spyLastAttemptTime;
    }

    public void setSpyLastAttemptTime(int spyLastAttemptTime) {
        this.spyLastAttemptTime = spyLastAttemptTime;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

    public void setRoundLimit(int roundLimit) {
        this.roundLimit = roundLimit;
    }

    public int getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public int getTimePerRound() {
        return timePerRound;
    }

    public void setTimePerRound(int timePerRound) {
        this.timePerRound = timePerRound;
    }
}
