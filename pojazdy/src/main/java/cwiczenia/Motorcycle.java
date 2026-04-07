package cwiczenia;

import java.util.Arrays;
import java.util.List;

public class Motorcycle extends Vehicle {

    private String category;


    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, String category) {
        super(id, brand, model, year, price, rented);

        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category must be set");
        }


        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toCSV() {
        return "MOTORCYCLE;" + id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented + ";" + category;
    }

    @Override
    public String toString() {
        return super.toString() + " category=" + category;
    }
}