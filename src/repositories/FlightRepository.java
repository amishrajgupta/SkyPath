package repositories;

import entities.Flight;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Flight data access.
 * Uses thread-safe concurrent structures for high-concurrency access.
 * Implements indexing by origin, destination, and date for efficient queries.
 */
public class FlightRepository {
    private final List<Flight> allFlights = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<Flight>> flightsByOrigin = new ConcurrentHashMap<>();
    private final Map<String, List<Flight>> flightsByDestination = new ConcurrentHashMap<>();
    private final Map<LocalDate, List<Flight>> flightsByDate = new ConcurrentHashMap<>();

    public void add(Flight flight) {
        if (flight == null) return;

        allFlights.add(flight);

        // Index by origin
        flightsByOrigin.computeIfAbsent(flight.getOrigin(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(flight);

        // Index by destination
        flightsByDestination.computeIfAbsent(flight.getDestination(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(flight);

        // Index by date
        LocalDate departureDate = flight.getDepartureTime().toLocalDate();
        flightsByDate.computeIfAbsent(departureDate, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(flight);
    }

    public List<Flight> findAll() {
        return new ArrayList<>(allFlights);
    }

    public List<Flight> findByOriginAndDestination(String origin, String destination) {
        if (origin == null || destination == null) {
            return Collections.emptyList();
        }

        List<Flight> result = new ArrayList<>();
        List<Flight> originsFlights = flightsByOrigin.get(origin);
        if (originsFlights != null) {
            for (Flight flight : originsFlights) {
                if (flight.getDestination().equals(destination)) {
                    result.add(flight);
                }
            }
        }
        return result;
    }

    public List<Flight> findByOriginAndDate(String origin, LocalDate date) {
        if (origin == null || date == null) {
            return Collections.emptyList();
        }

        List<Flight> result = new ArrayList<>();
        List<Flight> dateFlights = flightsByDate.get(date);
        if (dateFlights != null) {
            for (Flight flight : dateFlights) {
                if (flight.getOrigin().equals(origin)) {
                    result.add(flight);
                }
            }
        }
        return result;
    }

    public List<Flight> findByOriginDestinationAndDate(String origin, String destination, LocalDate date) {
        if (origin == null || destination == null || date == null) {
            return Collections.emptyList();
        }

        List<Flight> result = new ArrayList<>();
        List<Flight> dateFlights = flightsByDate.get(date);
        if (dateFlights != null) {
            for (Flight flight : dateFlights) {
                if (flight.getOrigin().equals(origin) && flight.getDestination().equals(destination)) {
                    result.add(flight);
                }
            }
        }
        return result;
    }

    public void clear() {
        allFlights.clear();
        flightsByOrigin.clear();
        flightsByDestination.clear();
        flightsByDate.clear();
    }

    public int size() {
        return allFlights.size();
    }
}

