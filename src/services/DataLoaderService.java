package services;

import entities.Airport;
import entities.Flight;
import repositories.AirportRepository;
import repositories.FlightRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for loading flight and airport data from JSON.
 * Uses simple regex-based JSON parsing to avoid external dependencies.
 * Thread-safe repositories for data storage.
 */
public class DataLoaderService {
    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    public DataLoaderService(AirportRepository airportRepository, FlightRepository flightRepository) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
    }

    /**
     * Load flights.json and populate repositories.
     */
    public void loadData(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));

        // Extract airports array
        String airportsJson = extractArray(content, "airports");
        loadAirports(airportsJson);

        // Extract flights array
        String flightsJson = extractArray(content, "flights");
        loadFlights(flightsJson);
    }

    private String extractArray(String json, String arrayName) {
        Pattern pattern = Pattern.compile("\"" + arrayName + "\"\\s*:\\s*\\[(.*?)\\]\\s*[,}]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private void loadAirports(String airportsJson) {
        // Split by objects and parse each
        String[] objects = airportsJson.split("\\}\\s*,\\s*\\{");

        for (String obj : objects) {
            try {
                obj = obj.replaceAll("[\\{\\}]", "").trim();
                if (obj.isEmpty()) continue;

                String code = extractJsonValue(obj, "code");
                String name = extractJsonValue(obj, "name");
                String city = extractJsonValue(obj, "city");
                String country = extractJsonValue(obj, "country");
                String timezone = extractJsonValue(obj, "timezone");

                if (code != null && !code.isEmpty()) {
                    Airport airport = new Airport(code, name, city, country, timezone);
                    airportRepository.add(airport);
                }
            } catch (Exception e) {
                System.err.println("Skipping malformed airport: " + e.getMessage());
            }
        }
    }

    private void loadFlights(String flightsJson) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String[] objects = flightsJson.split("\\}\\s*,\\s*\\{");

        for (String obj : objects) {
            try {
                obj = obj.replaceAll("[\\{\\}]", "").trim();
                if (obj.isEmpty()) continue;

                String flightNumber = extractJsonValue(obj, "flightNumber");
                String airline = extractJsonValue(obj, "airline");
                String origin = extractJsonValue(obj, "origin");
                String destination = extractJsonValue(obj, "destination");
                String departureTimeStr = extractJsonValue(obj, "departureTime");
                String arrivalTimeStr = extractJsonValue(obj, "arrivalTime");
                String priceStr = extractJsonValue(obj, "price");
                String aircraft = extractJsonValue(obj, "aircraft");

                if (flightNumber == null || origin == null || destination == null) {
                    continue;
                }

                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);
                double price = parseDouble(priceStr);

                Flight flight = new Flight(flightNumber, airline, origin, destination,
                        departureTime, arrivalTime, price, aircraft);
                flightRepository.add(flight);
            } catch (Exception e) {
                // Skip malformed flights silently
            }
        }
    }

    private String extractJsonValue(String json, String key) {
        // Try quoted string values first
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Try numeric values (for prices and other numbers)
        pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9.]+)");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            // Remove quotes if present
            value = value.replaceAll("[\"\\s]", "");
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}




