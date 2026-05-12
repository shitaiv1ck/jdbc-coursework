package com.spaceflights.model;

import java.math.BigDecimal;

public class Bag {
    private int id;
    private int idAstronaut;
    private BigDecimal flightWeight; // Может быть NULL в БД (нет NOT NULL)

    public Bag() {}

    public Bag(int id, int idAstronaut, BigDecimal flightWeight) {
        this.id = id;
        this.idAstronaut = idAstronaut;
        this.flightWeight = flightWeight;
    }

    public Bag(int idAstronaut, BigDecimal flightWeight) {
        this(0, idAstronaut, flightWeight);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdAstronaut() { return idAstronaut; }
    public void setIdAstronaut(int idAstronaut) { this.idAstronaut = idAstronaut; }
    public BigDecimal getFlightWeight() { return flightWeight; }
    public void setFlightWeight(BigDecimal flightWeight) { this.flightWeight = flightWeight; }

    @Override
    public String toString() {
        return String.format("Bag{id=%d, idAstronaut=%d, flightWeight=%s}", id, idAstronaut, flightWeight);
    }
}
