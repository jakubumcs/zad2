package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    private String id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, unique = true)
    private String plate;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean rented;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vehicle_attribute", joinColumns = @JoinColumn(name = "vehicle_id"))
    @MapKeyColumn(name = "attr_key")
    @Column(name = "attr_value")
    private Map<String, String> storedAttributes = new HashMap<>();

    @Transient
    private Map<String, Object> attributes = new HashMap<>();

    public Vehicle() {}

    public static Builder builder() {
        return new Builder();
    }

    public Vehicle(String category, String brand, String model, int year, String plate, double price) {
        this.id = UUID.randomUUID().toString();
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = false;
        this.storedAttributes = new HashMap<>();
        this.attributes = new HashMap<>();
    }

    public Vehicle(String id, String category, String brand, String model, int year,
                   String plate, double price, boolean rented, Map<String, Object> attributes) {
        this(id, category, brand, model, year, plate, price, rented, null, 0.0, 0.0, attributes);
    }

    public Vehicle(String id, String category, String brand, String model, int year,
                   String plate, double price, boolean rented, String locationName,
                   double latitude, double longitude, Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = rented;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        setAttributes(attributes);
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getPlate() { return plate; }
    public double getPrice() { return price; }
    public boolean isRented() { return rented; }
    public String getLocationName() { return locationName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Map<String, Object> getAttributes() {
        if ((attributes == null || attributes.isEmpty()) && storedAttributes != null && !storedAttributes.isEmpty()) {
            attributes = new HashMap<>(storedAttributes);
        }
        return attributes;
    }

    public Map<String, String> getStoredAttributes() { return storedAttributes; }

    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPlate(String plate) { this.plate = plate; }
    public void setPrice(double price) { this.price = price; }
    public void setRented(boolean rented) { this.rented = rented; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setLocation(String locationName, double latitude, double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        this.storedAttributes = new HashMap<>();
        if (attributes != null) {
            attributes.forEach((key, value) ->
                    this.storedAttributes.put(key, value == null ? null : String.valueOf(value)));
        }
    }

    public void addAttribute(String name, Object value) {
        this.attributes.put(name, value);
        this.storedAttributes.put(name, value == null ? null : String.valueOf(value));
    }

    public Object getAttribute(String key) {
        return getAttributes().get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
        storedAttributes.remove(key);
    }

    public Vehicle copy() {
        return new Vehicle(
                id,
                category,
                brand,
                model,
                year,
                plate,
                price,
                rented,
                locationName,
                latitude,
                longitude,
                getAttributes() == null ? new HashMap<>() : new HashMap<>(getAttributes())
        );
    }

    public static class Builder {
        private String id;
        private String category;
        private String brand;
        private String model;
        private int year;
        private String plate;
        private double price;
        private boolean rented;
        private String locationName;
        private double latitude;
        private double longitude;
        private Map<String, Object> attributes;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder plate(String plate) {
            this.plate = plate;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder rented(boolean rented) {
            this.rented = rented;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Vehicle build() {
            String finalId = id != null ? id : UUID.randomUUID().toString();
            return new Vehicle(
                    finalId,
                    category,
                    brand,
                    model,
                    year,
                    plate,
                    price,
                    rented,
                    locationName,
                    latitude,
                    longitude,
                    attributes == null ? new HashMap<>() : new HashMap<>(attributes)
            );
        }
    }

    @Override
    public String toString() {
        return "[" + category + "] " + brand + " " + model + " (" + year + ") rejestracja=" + plate +
                " cena=" + price + " wypożyczony=" + rented
                + " atrybuty=" + getAttributes() + " id=" + id;
    }
}
