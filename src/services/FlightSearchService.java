package services;

import entities.Itinerary;
import repositories.AirportRepository;
import repositories.FlightRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Flight search service with thread-safe concurrent request handling.
 * Uses ReadWriteLock to allow multiple concurrent searches while ensuring
 * data consistency during initialization.
 *
 * Design Pattern: Facade + Builder
 * - Provides a unified interface for flight search
 * - Delegates to ItineraryBuilderService for itinerary construction
 */
public class FlightSearchService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final ItineraryBuilderService itineraryBuilder;

    // ReadWriteLock allows multiple concurrent reads but exclusive writes
    private final ReadWriteLock searchLock = new ReentrantReadWriteLock();
    private volatile boolean initialized = false;

    public FlightSearchService(FlightRepository flightRepository, AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.itineraryBuilder = new ItineraryBuilderService(flightRepository, airportRepository);
    }

    /**
     * Mark the service as initialized.
     * Should be called after data loading.
     */
    public void markInitialized() {
        searchLock.writeLock().lock();
        try {
            initialized = true;
        } finally {
            searchLock.writeLock().unlock();
        }
    }

    /**
     * Search for flights from origin to destination on a specific date.
     * Thread-safe: multiple threads can call this simultaneously.
     *
     * Validation:
     * - Origin and destination must be different
     * - Both must be valid 3-letter IATA codes
     * - Date must be in ISO 8601 format (YYYY-MM-DD)
     *
     * @param origin Origin airport code (3 letters)
     * @param destination Destination airport code (3 letters)
     * @param date Travel date in YYYY-MM-DD format
     * @return List of valid itineraries sorted by duration
     * @throws IllegalArgumentException if validation fails
     */
    public List<Itinerary> search(String origin, String destination, String date)
            throws IllegalArgumentException {

        searchLock.readLock().lock();
        try {
            // Validate inputs
            validateSearchInput(origin, destination, date);

            LocalDate searchDate = LocalDate.parse(date);

            return itineraryBuilder.findItineraries(origin, destination, searchDate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Search failed: " + e.getMessage());
        } finally {
            searchLock.readLock().unlock();
        }
    }

    /**
     * Validate search parameters.
     */
    private void validateSearchInput(String origin, String destination, String date)
            throws IllegalArgumentException {

        // Validate format
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin airport code is required");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination airport code is required");
        }
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date is required in YYYY-MM-DD format");
        }

        // Validate IATA codes (3 letters, uppercase)
        if (!isValidIATACode(origin)) {
            throw new IllegalArgumentException("Invalid origin airport code: " + origin);
        }
        if (!isValidIATACode(destination)) {
            throw new IllegalArgumentException("Invalid destination airport code: " + destination);
        }

        // Validate same airport
        if (origin.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }

        // Validate airports exist in database
        if (!airportRepository.exists(origin.toUpperCase())) {
            throw new IllegalArgumentException("Origin airport not found: " + origin);
        }
        if (!airportRepository.exists(destination.toUpperCase())) {
            throw new IllegalArgumentException("Destination airport not found: " + destination);
        }

        // Validate date format
        try {
            LocalDate.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD");
        }
    }

    /**
     * Check if a string is a valid IATA code (3 uppercase letters).
     */
    private boolean isValidIATACode(String code) {
        if (code == null || code.length() != 3) {
            return false;
        }
        return code.matches("^[A-Z]{3}$");
    }

    /**
     * Get the number of loaded flights.
     */
    public int getLoadedFlightCount() {
        searchLock.readLock().lock();
        try {
            return flightRepository.size();
        } finally {
            searchLock.readLock().unlock();
        }
    }

    /**
     * Get the number of loaded airports.
     */
    public int getLoadedAirportCount() {
        searchLock.readLock().lock();
        try {
            return airportRepository.size();
        } finally {
            searchLock.readLock().unlock();
        }
    }
}


