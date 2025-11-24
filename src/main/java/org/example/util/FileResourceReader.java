package org.example.util;

import org.example.App;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileResourceReader {

    public static String readResourceToString(String path) throws URISyntaxException, IOException {
        Path resourcePath = Paths.get(Objects.requireNonNull(App.class.getResource(path)).toURI());
        return readResourceToString(resourcePath);

    }

    public static String readResourceToString(Path path) throws IOException {
        return Files.readString(path);
    }
}
