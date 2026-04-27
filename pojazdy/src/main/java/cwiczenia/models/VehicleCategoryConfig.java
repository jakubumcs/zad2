package cwiczenia.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleCategoryConfig {

    private String category;
    private Map<String, String> attributes = new HashMap<>();

    public VehicleCategoryConfig() {}

    public VehicleCategoryConfig(String category, Map<String, String> attributes) {
        this.category = category;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes == null ? new HashMap<>() : attributes;
    }

    public VehicleCategoryConfig copy() {
        return new VehicleCategoryConfig(category, new HashMap<>(attributes));
    }

    @Override
    public String toString() {
        return "VehicleCategoryConfig[category=" + category + " attributes=" + attributes + "]";
    }
}
