package entities;

/**
 * Represents a single flight segment within an itinerary.
 * Contains the flight details and layover information.
 */
public class FlightSegment {
    private final Flight flight;
    private final long layoverMinutes;

    public FlightSegment(Flight flight, long layoverMinutes) {
        this.flight = flight;
        this.layoverMinutes = layoverMinutes;
    }

    public Flight getFlight() {
        return flight;
    }

    public long getLayoverMinutes() {
        return layoverMinutes;
    }

    @Override
    public String toString() {
        String layoverInfo = layoverMinutes > 0 ? String.format(" (Layover: %d mins)", layoverMinutes) : "";
        return flight.toString() + layoverInfo;
    }
}

