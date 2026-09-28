package ru.govnomod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    public boolean enabled = false;
    /** Client-side right-click cooldown in ticks. 0 means no vanilla delay. */
    public int placementDelay = 0;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve("govnomod.json");

    public static int clampDelay(int delay) {
        return Math.max(0, Math.min(20, delay));
    }

    public static Config load() {
        try {
            if (Files.exists(PATH)) {
                Config config = GSON.fromJson(Files.readString(PATH), Config.class);
                if (config != null) {
                    config.placementDelay = clampDelay(config.placementDelay);
                    return config;
                }
            }
        } catch (Exception ignored) {
        }
        return new Config();
    }

    public void save() {
        placementDelay = clampDelay(placementDelay);
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
