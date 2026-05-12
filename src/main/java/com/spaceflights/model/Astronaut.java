package com.spaceflights.model;

import java.math.BigDecimal;

public class Astronaut {
    private int id;
    private Integer idCapsule; // Может быть NULL в БД
    private String role;
    private BigDecimal weight;
    private int height;

    public Astronaut() {}

    public Astronaut(int id, Integer idCapsule, String role, BigDecimal weight, int height) {
        this.id = id;
        this.idCapsule = idCapsule;
        this.role = role;
        this.weight = weight;
        this.height = height;
    }

    public Astronaut(Integer idCapsule, String role, BigDecimal weight, int height) {
        this(0, idCapsule, role, weight, height);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdCapsule() { return idCapsule; }
    public void setIdCapsule(Integer idCapsule) { this.idCapsule = idCapsule; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    @Override
    public String toString() {
        return String.format("Astronaut{id=%d, idCapsule=%s, role='%s', weight=%s, height=%d}", id, idCapsule, role, weight, height);
    }
}
