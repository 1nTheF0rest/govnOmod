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
    public boolean enabled = true;
    public double defaultCps = 20.0;
    public Map<String, Double> blockCps = new LinkedHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("govnomod.json");

    public double cpsFor(String blockId) {
        double cps = defaultCps;
        if (blockCps != null) {
            Double configured = blockCps.get(blockId);
            if (configured != null) {
                cps = configured;
            }
        }
        if (!Double.isFinite(cps) || cps <= 0.0) {
            cps = 20.0;
        }
        return Math.max(0.1, cps);
    }

    public static Config load() {
        try {
            if (Files.exists(PATH)) {
                Config config = GSON.fromJson(Files.readString(PATH), Config.class);
                if (config != null) {
                    if (config.blockCps == null) {
                        config.blockCps = new LinkedHashMap<>();
                    }
                    if (!Double.isFinite(config.defaultCps) || config.defaultCps <= 0.0) {
                        config.defaultCps = 20.0;
                    }
                    return config;
                }
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
