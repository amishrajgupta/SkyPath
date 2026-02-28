package utilities;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for timezone conversions and duration calculations.
 * Handles local airport times and conversions between different timezones.
 */
public class TimezoneUtils {

    /**
     * Convert a local time to UTC based on the timezone string.
     */
    public static ZonedDateTime toUTC(LocalDateTime localTime, String timezoneId) {
        try {
            ZoneId zoneId = ZoneId.of(timezoneId);
            ZonedDateTime zdt = localTime.atZone(zoneId);
            return zdt.withZoneSameInstant(ZoneId.of("UTC"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezoneId);
        }
    }

    /**
     * Calculate duration between two local times in different timezones.
     * Returns duration in minutes.
     */
    public static long calculateDurationInMinutes(
            LocalDateTime departureLocal, String originTimezone,
            LocalDateTime arrivalLocal, String destinationTimezone) {

        try {
            ZonedDateTime departurUTC = toUTC(departureLocal, originTimezone);
            ZonedDateTime arrivalUTC = toUTC(arrivalLocal, destinationTimezone);

            return ChronoUnit.MINUTES.between(departurUTC, arrivalUTC);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error calculating duration: " + e.getMessage());
        }
    }

    /**
     * Calculate layover duration between arrival and next departure.
     * Both times are in local airport times for their respective airports.
     */
    public static long calculateLayoverInMinutes(
            LocalDateTime arrivalLocal, String arrivalTimezone,
            LocalDateTime departureLocal, String departureTimezone) {

        try {
            ZonedDateTime arrivalUTC = toUTC(arrivalLocal, arrivalTimezone);
            ZonedDateTime departureUTC = toUTC(departureLocal, departureTimezone);

            return ChronoUnit.MINUTES.between(arrivalUTC, departureUTC);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error calculating layover: " + e.getMessage());
        }
    }

    /**
     * Format minutes to human-readable duration.
     */
    public static String formatDuration(long minutes) {
        long hours = minutes / 60;
        long mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }
}

