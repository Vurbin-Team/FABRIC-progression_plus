package com.progressionplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.progressionplus.upgrade.UpgradeType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpgradeConfigLoader {
    private static final File CONFIG_FILE = new File("config/progressionplus/upgrade_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefault();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);

            // Load upgrades
            UpgradeConfig.UPGRADE_SETTINGS.clear();
            for (Map.Entry<UpgradeType, UpgradeConfig.UpgradeSettings> entry : data.upgrades.entrySet()) {
                UpgradeType type = entry.getKey();
                UpgradeConfig.UPGRADE_SETTINGS.put(type, entry.getValue());
            }

            UpgradeConfig.setBaseCost(data.base_cost);
            UpgradeConfig.setLevelMultiplier(data.level_multiplier);
            UpgradeConfig.setMaxUpgradeLevel(data.max_upgrade_level);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveDefault() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            UpgradeConfig.init();

            ConfigData data = new ConfigData();
            data.base_cost = UpgradeConfig.getBaseCost();
            data.level_multiplier = UpgradeConfig.getLevelMultiplier();
            data.max_upgrade_level = UpgradeConfig.getMaxUpgradeLevel();
            data.upgrades = new EnumMap<>(UpgradeConfig.UPGRADE_SETTINGS);

            // Default HUD position same as renderer's default
            data.hud_x = -1; // will be set to defaults in screen init
            data.hud_y = -1;

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(int baseCost, int levelMultiplier, int maxUpgradeLevel, Map<UpgradeType, UpgradeConfig.UpgradeSettings> upgrades) {
        try {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("base_cost", baseCost);
            json.put("level_multiplier", levelMultiplier);
            json.put("max_upgrade_level", maxUpgradeLevel);

            Map<String, Map<String, Float>> upgradesJson = new LinkedHashMap<>();
            for (Map.Entry<UpgradeType, UpgradeConfig.UpgradeSettings> entry : upgrades.entrySet()) {
                Map<String, Float> upgradeValues = new LinkedHashMap<>();
                upgradeValues.put("bonusPerLevel", entry.getValue().bonusPerLevel);
                upgradeValues.put("resistancePerLevel", entry.getValue().resistancePerLevel);
                upgradesJson.put(entry.getKey().name(), upgradeValues);
            }
            json.put("upgrades", upgradesJson);

            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(json, writer);
            }

            load(); // reload to apply

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        int max_upgrade_level;
        int base_cost;
        int level_multiplier;
        Map<UpgradeType, UpgradeConfig.UpgradeSettings> upgrades;
        int hud_x;
        int hud_y;
    }
}
