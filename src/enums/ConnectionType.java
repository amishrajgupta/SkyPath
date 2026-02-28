package enums;

/**
 * Enum for connection types.
 * Used to determine layover duration requirements.
 */
public enum ConnectionType {
    DIRECT(0, "Direct flight"),
    DOMESTIC(45, "Domestic connection"),
    INTERNATIONAL(90, "International connection");

    private final int minimumLayoverMinutes;
    private final String description;

    ConnectionType(int minimumLayoverMinutes, String description) {
        this.minimumLayoverMinutes = minimumLayoverMinutes;
        this.description = description;
    }

    public int getMinimumLayoverMinutes() {
        return minimumLayoverMinutes;
    }

    public String getDescription() {
        return description;
    }
}

