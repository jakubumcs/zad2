package cwiczenia.models;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VehicleAttributeLabels {

    private VehicleAttributeLabels() {}

    public static String displayLabel(String key) {
        return switch (key) {
            case "fuelType" -> "typ paliwa";
            default -> key;
        };
    }

    public static Map<String, String> formatAttributeLabels(Map<String, String> attributes) {
        Map<String, String> formatted = new LinkedHashMap<>();
        attributes.forEach((key, value) -> formatted.put(displayLabel(key), value));
        return formatted;
    }

    public static Map<String, Object> formatAttributeValues(Map<String, Object> attributes) {
        Map<String, Object> formatted = new LinkedHashMap<>();
        attributes.forEach((key, value) -> formatted.put(displayLabel(key), value));
        return formatted;
    }
}
