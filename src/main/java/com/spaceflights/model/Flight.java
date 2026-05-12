package com.spaceflights.model;

import java.time.LocalDateTime;

public class Flight {
    private int id;
    private String destination;
    private LocalDateTime flightDate;
    private LocalDateTime arriveDate;

    public Flight() {}

    public Flight(int id, String destination, LocalDateTime flightDate, LocalDateTime arriveDate) {
        this.id = id;
        this.destination = destination;
        this.flightDate = flightDate;
        this.arriveDate = arriveDate;
    }

    public Flight(String destination, LocalDateTime flightDate, LocalDateTime arriveDate) {
        this(0, destination, flightDate, arriveDate);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getFlightDate() { return flightDate; }
    public void setFlightDate(LocalDateTime flightDate) { this.flightDate = flightDate; }
    public LocalDateTime getArriveDate() { return arriveDate; }
    public void setArriveDate(LocalDateTime arriveDate) { this.arriveDate = arriveDate; }

    @Override
    public String toString() {
        return String.format("Flight{id=%d, destination='%s', flightDate=%s, arriveDate=%s}", id, destination, flightDate, arriveDate);
    }
}
