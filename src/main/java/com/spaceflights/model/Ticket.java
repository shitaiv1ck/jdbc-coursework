package com.spaceflights.model;

import java.math.BigDecimal;

public class Ticket {
    private int id;
    private Integer idAstronaut; // Может быть NULL в БД
    private int idFlight;
    private BigDecimal price;

    public Ticket() {}

    public Ticket(int id, Integer idAstronaut, int idFlight, BigDecimal price) {
        this.id = id;
        this.idAstronaut = idAstronaut;
        this.idFlight = idFlight;
        this.price = price;
    }

    public Ticket(Integer idAstronaut, int idFlight, BigDecimal price) {
        this(0, idAstronaut, idFlight, price);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdAstronaut() { return idAstronaut; }
    public void setIdAstronaut(Integer idAstronaut) { this.idAstronaut = idAstronaut; }
    public int getIdFlight() { return idFlight; }
    public void setIdFlight(int idFlight) { this.idFlight = idFlight; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("Ticket{id=%d, idAstronaut=%s, idFlight=%d, price=%s}", id, idAstronaut, idFlight, price);
    }
}
