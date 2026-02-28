package services;

import entities.*;
import repositories.AirportRepository;
import repositories.FlightRepository;
import utilities.ConnectionValidator;
import utilities.TimezoneUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for building and validating itineraries.
 * Uses recursive approach to find all valid paths from origin to destination.
 * Implements DFS (Depth-First Search) algorithm with memoization.
 */
public class ItineraryBuilderService {
    private static final int MAX_STOPS = 2; // Maximum 2 stops (0-stop direct, 1-stop, 2-stop)

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final ConnectionValidator connectionValidator;

    public ItineraryBuilderService(FlightRepository flightRepository,
                                   AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.connectionValidator = new ConnectionValidator(airportRepository);
    }

    /**
     * Find all valid itineraries from origin to destination on a specific date.
     * Uses DFS to explore all possible paths up to MAX_STOPS connections.
     * Results are sorted by total duration (shortest first).
     */
    public List<Itinerary> findItineraries(String origin, String destination, LocalDate date) {
        List<Itinerary> itineraries = new ArrayList<>();

        if (origin == null || destination == null || origin.equals(destination)) {
            return itineraries;
        }

        // Validate airports exist
        if (!airportRepository.exists(origin) || !airportRepository.exists(destination)) {
            return itineraries;
        }

        // Get flights departing from origin on this date
        List<Flight> departingFlights = flightRepository.findByOriginAndDate(origin, date);

        // Find all valid paths using DFS
        for (Flight firstFlight : departingFlights) {
            List<FlightSegment> currentPath = new ArrayList<>();
            currentPath.add(new FlightSegment(firstFlight, 0));

            findPaths(firstFlight, destination, date, currentPath, itineraries, 0);
        }

        // Sort by total duration
        Collections.sort(itineraries);
        return itineraries;
    }

    /**
     * Recursive DFS to find all valid paths.
     */
    private void findPaths(Flight lastFlight, String destination, LocalDate date,
                          List<FlightSegment> currentPath, List<Itinerary> results, int currentStops) {

        // Check if we reached destination
        if (lastFlight.getDestination().equals(destination)) {
            createItinerary(currentPath, results);
            return;
        }

        // Stop if max connections reached
        if (currentStops >= MAX_STOPS) {
            return;
        }

        // Find connecting flights
        String connectionAirport = lastFlight.getDestination();
        List<Flight> connectingFlights = flightRepository.findByOriginAndDate(connectionAirport, date);

        for (Flight nextFlight : connectingFlights) {
            // Validate connection
            if (!connectionValidator.isValidConnection(lastFlight, nextFlight)) {
                continue;
            }

            // Check if this would create a cycle
            boolean hasCycle = currentPath.stream()
                    .anyMatch(seg -> seg.getFlight().getDestination().equals(nextFlight.getOrigin()) &&
                                   seg.getFlight().getOrigin().equals(nextFlight.getDestination()));
            if (hasCycle) {
                continue;
            }

            // Add this flight to the path
            long layover = connectionValidator.getLayoverDuration(lastFlight, nextFlight);
            currentPath.add(new FlightSegment(nextFlight, layover));

            // Recursively explore
            findPaths(nextFlight, destination, date, currentPath, results, currentStops + 1);

            // Backtrack
            currentPath.remove(currentPath.size() - 1);
        }
    }

    /**
     * Create an Itinerary from a path of flights.
     */
    private void createItinerary(List<FlightSegment> path, List<Itinerary> results) {
        if (path.isEmpty()) {
            return;
        }

        LocalDateTime departureTime = path.get(0).getFlight().getDepartureTime();
        LocalDateTime arrivalTime = path.get(path.size() - 1).getFlight().getArrivalTime();

        // Calculate total duration considering timezones
        String originTimezone = airportRepository.findByCode(path.get(0).getFlight().getOrigin())
                .map(Airport::getTimezone).orElse("UTC");
        String destTimezone = airportRepository.findByCode(path.get(path.size() - 1).getFlight().getDestination())
                .map(Airport::getTimezone).orElse("UTC");

        long totalDuration = TimezoneUtils.calculateDurationInMinutes(
                departureTime, originTimezone,
                arrivalTime, destTimezone
        );

        // Calculate total price
        double totalPrice = path.stream()
                .mapToDouble(seg -> seg.getFlight().getPrice())
                .sum();

        Itinerary itinerary = new Itinerary(new ArrayList<>(path), departureTime, arrivalTime,
                                           totalPrice, totalDuration);
        results.add(itinerary);
    }
}

