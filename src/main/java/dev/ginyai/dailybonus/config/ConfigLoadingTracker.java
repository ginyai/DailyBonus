package dev.ginyai.dailybonus.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ConfigLoadingTracker {
    public static final ConfigLoadingTracker INSTANCE = new ConfigLoadingTracker();

    private Map<String, Path> loadedFiles = new HashMap<>();

    private Path loadingDir;
    private Path loadingFile;

    public void loadDir(Path dir) {
        this.loadingDir = dir;
    }

    public void loadFile(Path file) {
        this.loadingFile = file;
        String key = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (loadedFiles.containsKey(key)) {
            throw new IllegalStateException("File with name " + file.getFileName() + " is already loaded at " + loadedFiles.get(key));
        }
    }

    public Path getLoadingDir() {
        return loadingDir;
    }

    public Path getLoadingFile() {
        return loadingFile;
    }

    public String getCurPrefix() {
        String s = loadingFile.getFileName().toString().toLowerCase(Locale.ROOT);
        return s.substring(0, s.length() - 5);
    }

}
