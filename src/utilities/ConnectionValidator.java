package utilities;

import entities.Flight;
import repositories.AirportRepository;
import enums.ConnectionType;

/**
 * Utility class for connection validation.
 * Validates layover times and connection rules.
 */
public class ConnectionValidator {
    private static final long MAX_LAYOVER_MINUTES = 6 * 60; // 6 hours
    private final AirportRepository airportRepository;

    public ConnectionValidator(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * Validate if a connection between two flights is valid.
     * Checks:
     * - No airport changes during layover
     * - Minimum layover time (45 min domestic, 90 min international)
     * - Maximum layover time (6 hours)
     */
    public boolean isValidConnection(Flight arrival, Flight departure) {
        // Same airport required for connection
        if (!arrival.getDestination().equals(departure.getOrigin())) {
            return false;
        }

        String connectionAirport = arrival.getDestination();
        String arrivalTimezone = airportRepository.findByCode(connectionAirport)
                .map(airport -> airport.getTimezone())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + connectionAirport));

        String departureTimezone = airportRepository.findByCode(departure.getOrigin())
                .map(airport -> airport.getTimezone())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + departure.getOrigin()));

        // Calculate layover time
        long layoverMinutes = TimezoneUtils.calculateLayoverInMinutes(
                arrival.getArrivalTime(), arrivalTimezone,
                departure.getDepartureTime(), departureTimezone
        );

        // Validate minimum layover
        ConnectionType connectionType = determineConnectionType(arrival, departure);
        if (layoverMinutes < connectionType.getMinimumLayoverMinutes()) {
            return false;
        }

        // Validate maximum layover
        if (layoverMinutes > MAX_LAYOVER_MINUTES) {
            return false;
        }

        return true;
    }

    /**
     * Determine connection type (domestic or international).
     */
    public ConnectionType determineConnectionType(Flight arrival, Flight departure) {
        String arrivalCountry = airportRepository.findByCode(arrival.getDestination())
                .map(airport -> airport.getCountry())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + arrival.getDestination()));

        String departureCountry = airportRepository.findByCode(departure.getOrigin())
                .map(airport -> airport.getCountry())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + departure.getOrigin()));

        // Both airports in same country = domestic
        if (arrivalCountry.equals(departureCountry)) {
            return ConnectionType.DOMESTIC;
        }

        return ConnectionType.INTERNATIONAL;
    }

    /**
     * Get layover duration for a connection.
     */
    public long getLayoverDuration(Flight arrival, Flight departure) {
        String connectionAirport = arrival.getDestination();
        String timezone = airportRepository.findByCode(connectionAirport)
                .map(airport -> airport.getTimezone())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + connectionAirport));

        return TimezoneUtils.calculateLayoverInMinutes(
                arrival.getArrivalTime(), timezone,
                departure.getDepartureTime(), timezone
        );
    }
}

