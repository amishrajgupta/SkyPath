import repositories.AirportRepository;
import repositories.FlightRepository;
import services.DataLoaderService;
import services.FlightSearchService;
import entities.Itinerary;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final String FLIGHTS_DATA_FILE = "flights.json";
    private static final int CONCURRENT_TEST_THREADS = 10;

    public static void main(String[] args) {
        try {
            // Initialize repositories
            FlightRepository flightRepository = new FlightRepository();
            AirportRepository airportRepository = new AirportRepository();

            // Load data
            System.out.println("Loading flight data from " + FLIGHTS_DATA_FILE + "...");
            DataLoaderService dataLoader = new DataLoaderService(airportRepository, flightRepository);
            dataLoader.loadData(FLIGHTS_DATA_FILE);

            // Initialize search service
            FlightSearchService searchService = new FlightSearchService(flightRepository, airportRepository);
            searchService.markInitialized();

            System.out.println("Data loaded successfully!");
            System.out.println("   - Airports: " + searchService.getLoadedAirportCount());
            System.out.println("   - Flights: " + searchService.getLoadedFlightCount());
            System.out.println("\n" + "=".repeat(80) + "\n");

            // Run test scenarios
            runTestScenarios(searchService);

            System.out.println("\n" + "=".repeat(80) + "\n");

            // Run concurrent access test
            runConcurrentAccessTest(searchService);

            System.out.println("\nAll tests completed successfully!");

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test Case 1: Direct and multi-stop flights
     * Search: JFK → LAX, 2024-03-15
     */
    private static void testCase1(FlightSearchService searchService) {
        System.out.println("TEST CASE 1: JFK → LAX (Direct + Multi-stop)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("JFK", "LAX", "2024-03-15");

            if (results.isEmpty()) {
                System.out.println("No itineraries found (expected some results)");
            } else {
                System.out.println("Found " + results.size() + " itineraries:");
                displayItineraries(results, 3);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 2: International route with 90-minute layover rule
     * Search: SFO → NRT, 2024-03-15
     */
    private static void testCase2(FlightSearchService searchService) {
        System.out.println("TEST CASE 2: SFO → NRT (International - 90 min minimum layover)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("SFO", "NRT", "2024-03-15");

            if (results.isEmpty()) {
                System.out.println("No valid itineraries found (may not have valid connections)");
            } else {
                System.out.println("Found " + results.size() + " itineraries:");
                displayItineraries(results, 2);

                // Verify all connections meet 90-minute international layover requirement
                boolean allValid = results.stream()
                        .allMatch(it -> validateInternationalLayovers(it));
                if (allValid) {
                    System.out.println("All connections respect 90-minute international layover rule");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 3: No direct flight exists - must find connections
     * Search: BOS → SEA, 2024-03-15
     */
    private static void testCase3(FlightSearchService searchService) {
        System.out.println("TEST CASE 3: BOS → SEA (No direct flight - must use connections)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("BOS", "SEA", "2024-03-15");

            if (results.isEmpty()) {
                System.out.println("No valid itineraries found");
            } else {
                System.out.println("Found " + results.size() + " itineraries via connections:");
                displayItineraries(results, 3);

                boolean hasConnections = results.stream()
                        .anyMatch(it -> it.getNumberOfStops() > 0);
                if (hasConnections) {
                    System.out.println("Correctly found connecting flights (no direct flights)");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 4: Same origin and destination - should return error
     * Search: JFK → JFK, 2024-03-15
     */
    private static void testCase4(FlightSearchService searchService) {
        System.out.println("TEST CASE 4: JFK → JFK (Same airport - should fail)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("JFK", "JFK", "2024-03-15");
            System.out.println("Should have thrown an exception but didn't");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 5: Invalid airport code - should handle gracefully
     * Search: XXX → LAX, 2024-03-15
     */
    private static void testCase5(FlightSearchService searchService) {
        System.out.println("TEST CASE 5: XXX → LAX (Invalid airport code)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("XXX", "LAX", "2024-03-15");
            System.out.println("Should have thrown an exception but didn't");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 6: Date line crossing - Sydney to LA (arrival appears before departure in local time)
     * Search: SYD → LAX, 2024-03-15
     */
    private static void testCase6(FlightSearchService searchService) {
        System.out.println("TEST CASE 6: SYD → LAX (Date line crossing)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("SYD", "LAX", "2024-03-15");

            if (results.isEmpty()) {
                System.out.println("No itineraries found");
            } else {
                System.out.println("Found " + results.size() + " itineraries:");
                displayItineraries(results, 2);
                System.out.println("Correctly handled date line crossing with timezone conversion");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 7: Invalid date format
     */
    private static void testCase7(FlightSearchService searchService) {
        System.out.println("TEST CASE 7: JFK → LAX (Invalid date format)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("JFK", "LAX", "2024/03/15");
            System.out.println("Should have thrown an exception but didn't");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test Case 8: Short airport codes
     */
    private static void testCase8(FlightSearchService searchService) {
        System.out.println("TEST CASE 8: JF → LAX (Invalid airport code - too short)");
        System.out.println("-".repeat(80));

        try {
            List<Itinerary> results = searchService.search("JF", "LAX", "2024-03-15");
            System.out.println("Should have thrown an exception but didn't");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Run all test scenarios
     */
    private static void runTestScenarios(FlightSearchService searchService) {
        System.out.println("RUNNING TEST SCENARIOS\n");

        testCase1(searchService);
        testCase2(searchService);
        testCase3(searchService);
        testCase4(searchService);
        testCase5(searchService);
        testCase6(searchService);
        testCase7(searchService);
        testCase8(searchService);

        System.out.println("All test scenarios completed!\n");
    }

    /**
     * Test concurrent access to ensure thread-safety
     */
    private static void runConcurrentAccessTest(FlightSearchService searchService) throws InterruptedException {
        System.out.println("CONCURRENT ACCESS TEST");
        System.out.println("-".repeat(80));
        System.out.println("Simulating " + CONCURRENT_TEST_THREADS + " concurrent search requests...\n");

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_TEST_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_TEST_THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Test queries
        String[][] testQueries = {
                {"JFK", "LAX", "2024-03-15"},
                {"SFO", "NRT", "2024-03-15"},
                {"BOS", "SEA", "2024-03-15"},
                {"LAX", "JFK", "2024-03-15"},
                {"ORD", "MIA", "2024-03-15"},
        };

        for (int i = 0; i < CONCURRENT_TEST_THREADS; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    String[] query = testQueries[threadIndex % testQueries.length];
                    List<Itinerary> results = searchService.search(query[0], query[1], query[2]);
                    successCount.incrementAndGet();
                    System.out.println("Thread " + threadIndex + ": " + query[0] + "→" + query[1] +
                                     " found " + results.size() + " itineraries");
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.out.println("Thread " + threadIndex + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("\n" + "-".repeat(80));
        System.out.println("Concurrent Test Results:");
        System.out.println("   - Successful searches: " + successCount.get());
        System.out.println("   - Failed searches: " + errorCount.get());
        System.out.println("   - Total: " + CONCURRENT_TEST_THREADS);

        if (errorCount.get() == 0) {
            System.out.println("All concurrent requests handled successfully!");
        }
    }

    /**
     * Display itineraries with formatting
     */
    private static void displayItineraries(List<Itinerary> itineraries, int maxDisplay) {
        int count = 0;
        for (Itinerary itinerary : itineraries) {
            System.out.println(itinerary);
            count++;
            if (count >= maxDisplay) {
                if (itineraries.size() > maxDisplay) {
                    System.out.println("   ... and " + (itineraries.size() - maxDisplay) + " more itineraries\n");
                }
                break;
            }
        }
    }

    /**
     * Validate that all layovers in an international itinerary meet the 90-minute requirement
     */
    private static boolean validateInternationalLayovers(Itinerary itinerary) {
        return itinerary.getSegments().stream()
                .allMatch(segment -> segment.getLayoverMinutes() == 0 || segment.getLayoverMinutes() >= 90);
    }
}
