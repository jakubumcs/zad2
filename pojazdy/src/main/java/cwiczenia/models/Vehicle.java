package cwiczenia.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vehicle {

    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private boolean rented;
    private Map<String, String> attributes = new HashMap<>();

    public Vehicle() {}

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
                   String plate, double price, boolean rented, Map<String, String> attributes) {
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
    public Map<String, String> getAttributes() { return attributes; }

    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPlate(String plate) { this.plate = plate; }
    public void setPrice(double price) { this.price = price; }
    public void setRented(boolean rented) { this.rented = rented; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    @Override
    public String toString() {
        return "[" + category + "] " + brand + " " + model + " (" + year + ") plate=" + plate +
                " price=" + price + " rented=" + rented + " attributes=" + attributes + " id=" + id;
    }
}
