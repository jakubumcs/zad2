package cwiczenia;
public class Car extends Vehicle {

    public Car(String id, String brand, String model, int year, double price, boolean rented) {
        super(id, brand, model, year, price, rented);
    }

    @Override
    public String toCSV() {
        return "CAR;" + id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }
}