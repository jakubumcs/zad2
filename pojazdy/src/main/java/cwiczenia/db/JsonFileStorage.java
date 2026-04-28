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

        Path moduleDir = findModuleDir(Paths.get("").toAbsolutePath());
        if (moduleDir != null) {
            return moduleDir.resolve(fileName).toFile();
        }

        return direct.toFile();
    }

    private Path findModuleDir(Path start) {
        Path current = start;
        for (int i = 0; i < 6 && current != null; i++) {
            if (current.resolve("pom.xml").toFile().exists()) {
                return current;
            }

            Path nestedModule = current.resolve("pojazdy");
            if (nestedModule.resolve("pom.xml").toFile().exists()) {
                return nestedModule;
            }

            current = current.getParent();
        }

        return null;
    }
}
