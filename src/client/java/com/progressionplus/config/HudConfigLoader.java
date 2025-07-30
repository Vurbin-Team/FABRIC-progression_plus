package com.progressionplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class HudConfigLoader {
    private static final File CONFIG_FILE = new File("config/progressionplus/hud_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Размеры HUD, повинні відповідати реальним у CustomHudRenderer
    public static final int HUD_WIDTH = 75;
    public static final int HUD_HEIGHT = 38;

    private static int hudX = Integer.MIN_VALUE;
    private static int hudY = Integer.MIN_VALUE;

    // Завантаження конфігурації з файлу
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefault();
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            HudData data = GSON.fromJson(reader, HudData.class);
            hudX = data.hud_x;
            hudY = data.hud_y;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Збереження координат у файл
    public static void save(int x, int y) {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            Map<String, Integer> map = new LinkedHashMap<>();
            map.put("hud_x", x);
            map.put("hud_y", y);
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(map, writer);
            }
            hudX = x;
            hudY = y;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Отримати X: якщо невстановлений — обчислити за замовчуванням
    public static int getHudX(int screenWidth) {
        if (hudX == Integer.MIN_VALUE) {
            return screenWidth - HUD_WIDTH;
        }
        return hudX;
    }

    // Отримати Y: якщо невстановлений — обчислити за замовчуванням
    public static int getHudY(int screenHeight) {
        if (hudY == Integer.MIN_VALUE) {
            return (int) (screenHeight / 1.5f);
        }
        return hudY;
    }

    public static boolean isOnRight(int screenWidth) {
        return getHudX(screenWidth) > screenWidth / 2;
    }

    // Налаштування початкового файлу
    private static void saveDefault() {
        save(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    // Внутрішній клас для GSON
    private static class HudData {
        int hud_x;
        int hud_y;
    }

}
