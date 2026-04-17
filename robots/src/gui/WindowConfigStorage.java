package gui;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;


public class WindowConfigStorage {
    private static final String CONFIG_FILENAME = ".java_game_windows.dat";

    public static void saveConfig(List<WindowConfig> configs) throws IOException {
        Path path = Path.of(System.getProperty("user.home"), CONFIG_FILENAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(configs);
        }
    }

    public static List<WindowConfig> loadConfig() throws IOException, ClassNotFoundException {
        Path path = Path.of(System.getProperty("user.home"), CONFIG_FILENAME);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            @SuppressWarnings("unchecked")
            List<WindowConfig> result = (List<WindowConfig>) ois.readObject();
            return result;
        }
    }
}