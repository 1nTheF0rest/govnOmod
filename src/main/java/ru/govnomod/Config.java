package ru.govnomod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Config {
    public double defaultCps = 20.0;
    public Map<String, Double> blockCps = new LinkedHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("govnomod.json");

    public double cpsFor(String blockId) {
        return Math.max(0.1, blockCps.getOrDefault(blockId, defaultCps));
    }

    public static Config load() {
        try {
            if (Files.exists(PATH)) {
                Config config = GSON.fromJson(Files.readString(PATH), Config.class);
                return config == null ? new Config() : config;
            }
        } catch (Exception ignored) {
        }
        return new Config();
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
