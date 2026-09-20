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
    public boolean holdMode = true;
    public boolean leftEnabled = false;
    public boolean rightEnabled = true;
    public int leftCps = 10;
    public int rightCps = 20;
    public Map<String, ItemSetting> itemSettings = new LinkedHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("govnomod.json");

    public static final class ItemSetting {
        public boolean enabled = true;
        public int leftCps = 10;
        public int rightCps = 20;

        public ItemSetting() {}

        public ItemSetting(int leftCps, int rightCps) {
            this.leftCps = clampCps(leftCps);
            this.rightCps = clampCps(rightCps);
        }
    }

    public static int clampCps(int cps) {
        return Math.max(1, Math.min(200, cps));
    }

    public ItemSetting settingFor(String itemId) {
        return itemSettings == null ? null : itemSettings.get(itemId);
    }

    public int leftCpsFor(String itemId) {
        ItemSetting setting = settingFor(itemId);
        return setting != null && setting.enabled ? clampCps(setting.leftCps) : clampCps(leftCps);
    }

    public int rightCpsFor(String itemId) {
        ItemSetting setting = settingFor(itemId);
        return setting != null && setting.enabled ? clampCps(setting.rightCps) : clampCps(rightCps);
    }

    public static Config load() {
        try {
            if (Files.exists(PATH)) {
                Config config = GSON.fromJson(Files.readString(PATH), Config.class);
                if (config != null) {
                    if (config.itemSettings == null) config.itemSettings = new LinkedHashMap<>();
                    config.leftCps = clampCps(config.leftCps);
                    config.rightCps = clampCps(config.rightCps);
                    for (ItemSetting setting : config.itemSettings.values()) {
                        if (setting != null) {
                            setting.leftCps = clampCps(setting.leftCps);
                            setting.rightCps = clampCps(setting.rightCps);
                        }
                    }
                    return config;
                }
            }
        } catch (Exception ignored) {
        }
        return new Config();
    }

    public void save() {
        leftCps = clampCps(leftCps);
        rightCps = clampCps(rightCps);
        if (itemSettings == null) itemSettings = new LinkedHashMap<>();
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
