package entities;

/**
 * Represents an airport with its details.
 * Immutable entity for thread-safe access.
 */
public class Airport {
    private final String code;
    private final String name;
    private final String city;
    private final String country;
    private final String timezone;

    public Airport(String code, String name, String city, String country, String timezone) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s)", code, city, country);
    }
}

