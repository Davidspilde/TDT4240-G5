package io.github.Spyfall.message.request;

public class LobbyOptionsMessage extends RequestMessage {
    private int roundLimit;
    private int locationNumber;
    private int maxPlayerCount;
    private int timePerRound;
    private int spyLastAttemptTime;

    public LobbyOptionsMessage(String username, String lobbyCode, int roundLimit, int locationNumber,
            int maxPlayerCount, int timePerRound, int spyLastAttemptTime) {
        super("updateOptions", username, lobbyCode);
        this.roundLimit = roundLimit;
        this.locationNumber = locationNumber;
        this.maxPlayerCount = maxPlayerCount;
        this.timePerRound = timePerRound;
        this.spyLastAttemptTime = spyLastAttemptTime;
    }
}
