package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private boolean rented;
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
    }

    public Vehicle(String id, String category, String brand, String model, int year,
                   String plate, double price, boolean rented, Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = rented;
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getPlate() { return plate; }
    public double getPrice() { return price; }
    public boolean isRented() { return rented; }
    public Map<String, Object> getAttributes() { return attributes; }

    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPlate(String plate) { this.plate = plate; }
    public void setPrice(double price) { this.price = price; }
    public void setRented(boolean rented) { this.rented = rented; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public void addAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
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
                attributes == null ? new HashMap<>() : new HashMap<>(attributes)
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
                    attributes == null ? new HashMap<>() : new HashMap<>(attributes)
            );
        }
    }

    @Override
    public String toString() {
        return "[" + category + "] " + brand + " " + model + " (" + year + ") rejestracja=" + plate +
                " cena=" + price + " wypożyczony=" + rented
                + " atrybuty=" + attributes + " id=" + id;
    }
}
