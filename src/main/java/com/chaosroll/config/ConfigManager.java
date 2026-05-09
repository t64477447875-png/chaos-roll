package com.chaosroll.config;

import com.chaosroll.ChaosRollMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    private static final String FILE_NAME = "chaos_roll_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static volatile ChaosRollConfig CONFIG = new ChaosRollConfig();
    private static Path filePath;

    private ConfigManager() {}

    public static ChaosRollConfig get() {
        return CONFIG;
    }

    public static void init() {
        filePath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        load();
    }

    public static synchronized void load() {
        if (filePath == null) {
            filePath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        }
        if (!Files.exists(filePath)) {
            CONFIG = new ChaosRollConfig();
            save();
            ChaosRollMod.LOGGER.info("[Chaos Roll] Created default config at {}", filePath);
            return;
        }
        try (Reader r = Files.newBufferedReader(filePath)) {
            ChaosRollConfig parsed = GSON.fromJson(r, ChaosRollConfig.class);
            if (parsed == null) {
                parsed = new ChaosRollConfig();
            }
            parsed.validate();
            CONFIG = parsed;
            save();
            ChaosRollMod.LOGGER.info("[Chaos Roll] Loaded config from {}", filePath);
        } catch (JsonSyntaxException ex) {
            ChaosRollMod.LOGGER.error("[Chaos Roll] Config syntax error, using defaults: {}", ex.getMessage());
            CONFIG = new ChaosRollConfig();
        } catch (IOException ex) {
            ChaosRollMod.LOGGER.error("[Chaos Roll] Failed to read config: {}", ex.getMessage());
            CONFIG = new ChaosRollConfig();
        }
    }

    public static synchronized void save() {
        if (filePath == null) {
            filePath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        }
        try {
            Files.createDirectories(filePath.getParent());
            try (Writer w = Files.newBufferedWriter(filePath)) {
                GSON.toJson(CONFIG, w);
            }
        } catch (IOException ex) {
            ChaosRollMod.LOGGER.error("[Chaos Roll] Failed to write config: {}", ex.getMessage());
        }
    }

    public static synchronized void reload() {
        load();
    }
}
