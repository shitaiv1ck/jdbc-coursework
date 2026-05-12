package com.spaceflights.model;

public class Food {
    private int id;
    private Integer idShips; // Может быть NULL в БД
    private String contents;

    public Food() {}

    public Food(int id, Integer idShips, String contents) {
        this.id = id;
        this.idShips = idShips;
        this.contents = contents;
    }

    public Food(Integer idShips, String contents) {
        this(0, idShips, contents);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdShips() { return idShips; }
    public void setIdShips(Integer idShips) { this.idShips = idShips; }
    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }

    @Override
    public String toString() {
        return String.format("Food{id=%d, idShips=%s, contents='%s'}", id, idShips, contents);
    }
}
