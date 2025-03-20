package com.interloperServer.interloperServer.model;


public class Round {
    private final int roundNumber;
    private final String location;
    private boolean isActive;

    public Round(int roundNumber) {
        this.roundNumber = roundNumber;
        this.location = generateRandomLocation();
        this.isActive = true;
    }

    public int getRoundNumber() { return roundNumber; }
    public String getLocation() { return location; }
    public boolean isActive() { return isActive; }

    public void endRound() { this.isActive = false; }

    private String generateRandomLocation() {
        String[] locations = {"Restaurant", "Museum", "Beach", "Space Station", "Jungle"};
        return locations[new java.util.Random().nextInt(locations.length)];
    }
}
