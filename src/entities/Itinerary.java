package entities;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a complete itinerary from origin to destination.
 * Can contain direct flights or multi-stop connections.
 * Immutable entity for thread-safe access.
 */
public class Itinerary implements Comparable<Itinerary> {
    private final List<FlightSegment> segments;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final double totalPrice;
    private final long totalDurationMinutes;

    public Itinerary(List<FlightSegment> segments, LocalDateTime departureTime,
                     LocalDateTime arrivalTime, double totalPrice, long totalDurationMinutes) {
        this.segments = segments;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalPrice = totalPrice;
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public List<FlightSegment> getSegments() {
        return segments;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public long getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public int getNumberOfStops() {
        return segments.size() - 1;
    }

    @Override
    public int compareTo(Itinerary other) {
        return Long.compare(this.totalDurationMinutes, other.totalDurationMinutes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n=== Itinerary (%d stop%s) ===\n",
                getNumberOfStops(), getNumberOfStops() == 1 ? "" : "s"));

        for (int i = 0; i < segments.size(); i++) {
            FlightSegment segment = segments.get(i);
            sb.append(String.format("  Leg %d: %s\n", i + 1, segment.toString()));
        }

        long hours = totalDurationMinutes / 60;
        long mins = totalDurationMinutes % 60;

        sb.append(String.format("\n  Departure: %s\n", departureTime));
        sb.append(String.format("  Arrival:   %s\n", arrivalTime));
        sb.append(String.format("  Duration:  %d:%02d\n", hours, mins));
        sb.append(String.format("  Total Price: $%.2f\n", totalPrice));

        return sb.toString();
    }
}

