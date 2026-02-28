package entities;

import java.time.LocalDateTime;

/**
 * Represents a flight segment with departure and arrival details.
 * Immutable entity for thread-safe access.
 */
public class Flight {
    private final String flightNumber;
    private final String airline;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final double price;
    private final String aircraft;

    public Flight(String flightNumber, String airline, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, double price, String aircraft) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.aircraft = aircraft;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public String getAircraft() {
        return aircraft;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s | Depart: %s | Arrive: %s | $%.2f",
                flightNumber, origin, destination, departureTime, arrivalTime, price);
    }
}

