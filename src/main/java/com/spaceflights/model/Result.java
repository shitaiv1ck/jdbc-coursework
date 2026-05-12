package com.spaceflights.model;

public class Result {
    // Составной первичный ключ, поле id отсутствует
    private int idTest;
    private int idAstronaut;
    private Boolean completed;

    public Result() {}

    public Result(int idTest, int idAstronaut, Boolean completed) {
        this.idTest = idTest;
        this.idAstronaut = idAstronaut;
        this.completed = completed;
    }

    public int getIdTest() { return idTest; }
    public void setIdTest(int idTest) { this.idTest = idTest; }
    public int getIdAstronaut() { return idAstronaut; }
    public void setIdAstronaut(int idAstronaut) { this.idAstronaut = idAstronaut; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return String.format("Result{idTest=%d, idAstronaut=%d, completed=%s}", idTest, idAstronaut, completed);
    }
}









