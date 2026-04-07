package cwiczenia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepositoryImpl implements IVehicleRepository {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final String FILE_NAME;

    public VehicleRepositoryImpl() {
        this("vehicles.csv");
    }

    public VehicleRepositoryImpl(String fileName) {
        this.FILE_NAME = fileName;
        load();
    }

    @Override
    public void add(Vehicle vehicle) {
        vehicles.add(vehicle);
        save();
    }

    @Override
    public void remove(String id) {
        boolean removed = vehicles.removeIf(v -> v.getId().equals(id));
        if (removed) {
            save();
        }
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                if (v instanceof Car c) {
                    return new Car(
                            c.getId(),
                            c.getBrand(),
                            c.getModel(),
                            c.getYear(),
                            c.getPrice(),
                            c.isRented()
                    );
                } else if (v instanceof Motorcycle m) {
                    return new Motorcycle(
                            m.getId(),
                            m.getBrand(),
                            m.getModel(),
                            m.getYear(),
                            m.getPrice(),
                            m.isRented(),
                            m.getCategory()
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id) && !v.isRented()) {
                v.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id) && v.isRented()) {
                v.setRented(false);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v instanceof Car c) {
                copy.add(new Car(
                        c.getId(),
                        c.getBrand(),
                        c.getModel(),
                        c.getYear(),
                        c.getPrice(),
                        c.isRented()
                ));
            } else if (v instanceof Motorcycle m) {
                copy.add(new Motorcycle(
                        m.getId(),
                        m.getBrand(),
                        m.getModel(),
                        m.getYear(),
                        m.getPrice(),
                        m.isRented(),
                        m.getCategory()
                ));
            }
        }
        return copy;
    }


    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Vehicle v : vehicles) {
                writer.println(v.toCSV());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    public void load() {
        vehicles.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 7) continue;

                if ("CAR".equals(parts[0])) {
                    vehicles.add(new Car(
                            parts[1],
                            parts[2],
                            parts[3],
                            Integer.parseInt(parts[4]),
                            Double.parseDouble(parts[5]),
                            Boolean.parseBoolean(parts[6])
                    ));
                } else if ("MOTORCYCLE".equals(parts[0]) && parts.length >= 8) {
                    vehicles.add(new Motorcycle(
                            parts[1],
                            parts[2],
                            parts[3],
                            Integer.parseInt(parts[4]),
                            Double.parseDouble(parts[5]),
                            Boolean.parseBoolean(parts[6]),
                            parts[7]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }
}