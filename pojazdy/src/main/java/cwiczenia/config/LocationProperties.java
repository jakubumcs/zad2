package cwiczenia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "carrent.location")
public class LocationProperties {

    private List<Allowed> allowed = new ArrayList<>();
    private List<Allowed> outOfBounds = new ArrayList<>();
    private long schedulerIntervalMs = 300000;

    public List<Allowed> getAllowed() { return allowed; }
    public void setAllowed(List<Allowed> allowed) { this.allowed = allowed; }

    public List<Allowed> getOutOfBounds() { return outOfBounds; }
    public void setOutOfBounds(List<Allowed> outOfBounds) { this.outOfBounds = outOfBounds; }

    public long getSchedulerIntervalMs() { return schedulerIntervalMs; }
    public void setSchedulerIntervalMs(long schedulerIntervalMs) { this.schedulerIntervalMs = schedulerIntervalMs; }

    public boolean isAllowedLocation(String locationName) {
        if (locationName == null) return false;
        return allowed.stream().anyMatch(a -> a.getName().equalsIgnoreCase(locationName));
    }

    public static class Allowed {
        private String name;
        private double latitude;
        private double longitude;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}
