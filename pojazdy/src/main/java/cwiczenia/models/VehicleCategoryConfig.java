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

    public static Builder builder() {
        return new Builder();
    }

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
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public void addAttribute(String name, String type) {
        attributes.put(name, type);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public VehicleCategoryConfig copy() {
        return new VehicleCategoryConfig(category, new HashMap<>(attributes));
    }

    public static class Builder {
        private String category;
        private Map<String, String> attributes;

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public VehicleCategoryConfig build() {
            return new VehicleCategoryConfig(category, attributes);
        }
    }
}
