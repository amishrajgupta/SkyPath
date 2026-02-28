package repositories;

import entities.Airport;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Airport data access.
 * Uses thread-safe ConcurrentHashMap for concurrent access.
 */
public class AirportRepository {
    private final Map<String, Airport> airportsByCode = new ConcurrentHashMap<>();

    public void add(Airport airport) {
        if (airport != null && airport.getCode() != null) {
            airportsByCode.put(airport.getCode(), airport);
        }
    }

    public Optional<Airport> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(airportsByCode.get(code));
    }

    public List<Airport> findAll() {
        return new ArrayList<>(airportsByCode.values());
    }

    public boolean exists(String code) {
        return code != null && airportsByCode.containsKey(code);
    }

    public void clear() {
        airportsByCode.clear();
    }

    public int size() {
        return airportsByCode.size();
    }
}

