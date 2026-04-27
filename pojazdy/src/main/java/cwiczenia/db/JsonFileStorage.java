package cwiczenia.db;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStorage<T> {

    private final ObjectMapper mapper;
    private final String fileName;
    private final Class<T> elementType;

    public JsonFileStorage(ObjectMapper mapper, String fileName, Class<T> elementType) {
        this.mapper = mapper;
        this.fileName = fileName;
        this.elementType = elementType;
    }

    public List<T> load() {
        File file = resolveFile();
        if (!file.exists()) return new ArrayList<>();

        try {
            JavaType listType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType);
            List<T> loaded = mapper.readValue(file, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void save(List<T> data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(resolveFile(), data);
        } catch (Exception e) {
            System.err.println("Error saving " + fileName + ": " + e.getMessage());
        }
    }

    public String resolvedPath() {
        return resolveFile().getAbsolutePath();
    }

    private File resolveFile() {
        Path direct = Paths.get(fileName);
        if (direct.toFile().exists() || direct.isAbsolute()) {
            return direct.toFile();
        }

        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 4 && current != null; i++) {
            Path candidate = current.resolve(fileName);
            if (candidate.toFile().exists()) {
                return candidate.toFile();
            }

            Path moduleCandidate = current.resolve("pojazdy").resolve(fileName);
            if (moduleCandidate.toFile().exists()) {
                return moduleCandidate.toFile();
            }

            current = current.getParent();
        }

        return direct.toFile();
    }
}
