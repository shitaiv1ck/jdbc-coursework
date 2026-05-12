package com.spaceflights.model;

public class Capsule {
    private int id;
    private Integer idShips; // Может быть NULL в БД
    private String capsuleClass; // 'class' зарезервировано, поэтому capsuleClass

    public Capsule() {}

    public Capsule(int id, Integer idShips, String capsuleClass) {
        this.id = id;
        this.idShips = idShips;
        this.capsuleClass = capsuleClass;
    }

    public Capsule(Integer idShips, String capsuleClass) {
        this(0, idShips, capsuleClass);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdShips() { return idShips; }
    public void setIdShips(Integer idShips) { this.idShips = idShips; }
    public String getCapsuleClass() { return capsuleClass; }
    public void setCapsuleClass(String capsuleClass) { this.capsuleClass = capsuleClass; }

    @Override
    public String toString() {
        return String.format("Capsule{id=%d, idShips=%s, capsuleClass='%s'}", id, idShips, capsuleClass);
    }
}
