package io.github.Spyfall.models.messages;

public class LobbyOptionsMessage extends Message {
    private int roundLimit;
    private int locationNumber;
    private int maxPlayerCount;
    private int timePerRound;

    public LobbyOptionsMessage(String username, String lobbyCode, int roundLimit, int locationNumber,
            int maxPlayerCount, int timePerRound) {
        super("updateOptions", username, lobbyCode);
        this.roundLimit = roundLimit;
        this.locationNumber = locationNumber;
        this.maxPlayerCount = maxPlayerCount;
        this.timePerRound = timePerRound;
    }
}
