package com.spaceflights.model;

public class Spaceship {
    private int id;
    private int idFlight;
    private int capsulesCount;

    public Spaceship() {}

    public Spaceship(int id, int idFlight, int capsulesCount) {
        this.id = id;
        this.idFlight = idFlight;
        this.capsulesCount = capsulesCount;
    }

    public Spaceship(int idFlight, int capsulesCount) {
        this(0, idFlight, capsulesCount);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdFlight() { return idFlight; }
    public void setIdFlight(int idFlight) { this.idFlight = idFlight; }
    public int getCapsulesCount() { return capsulesCount; }
    public void setCapsulesCount(int capsulesCount) { this.capsulesCount = capsulesCount; }

    @Override
    public String toString() {
        return String.format("Spaceship{id=%d, idFlight=%d, capsulesCount=%d}", id, idFlight, capsulesCount);
    }
}
