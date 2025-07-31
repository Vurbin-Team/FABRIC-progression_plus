package com.progressionplus.data;

import com.progressionplus.Progressionplus;
import com.progressionplus.upgrade.UpgradeType;
import com.progressionplus.upgrades.PlayerUpgrade;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static com.progressionplus.Progressionplus.LOGGER;

public class PlayerUpgradeData implements Component, CopyableComponent<PlayerUpgradeData> {
    private static final Identifier UPGRADE_KEY = Identifier.of(Progressionplus.MOD_ID, "upgrades");
    private final PlayerUpgrade playerUpgrade;
    private final PlayerEntity player;

    public PlayerUpgradeData(PlayerEntity player) {
        this.playerUpgrade = new PlayerUpgrade();
        this.player = player;
        // Safely get player name, fallback to UUID if name is not available
        String playerIdentifier = player.getGameProfile() != null ?
                player.getGameProfile().getName() :
                player.getUuid().toString();
        LOGGER.info("Created PlayerUpgradeData for player: {}", playerIdentifier);
    }

    public PlayerUpgrade getPlayerUpgrade() {
        return playerUpgrade;
    }

    public void logUpgrades(PlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        LOGGER.info("Player {} upgrades:", playerName);
        for (UpgradeType type : UpgradeType.values()) {
            int level = playerUpgrade.getLevel(type);
            LOGGER.info(" - {}: Level {}", type.getDisplayName(), level);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if (nbtCompound.contains(UPGRADE_KEY.toString())) {
            Map<String, Integer> upgrades = new HashMap<>();

            for (String key : nbtCompound.getKeys()) {
                if (!key.equals(UPGRADE_KEY.toString())) {
                    int value = nbtCompound.getInt(key); // This returns a primitive int
                    upgrades.put(key, value);
                }
            }

            playerUpgrade.loadUpgrades(upgrades);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        Map<String, Integer> upgrades = playerUpgrade.getUpgrades();

        for (Map.Entry<String, Integer> entry : upgrades.entrySet()) {
            nbtCompound.putInt(entry.getKey(), entry.getValue());
        }

        nbtCompound.putString(UPGRADE_KEY.toString(), "upgrades");
    }

    @Override
    public void copyFrom(PlayerUpgradeData playerUpgradeData) {
        if (playerUpgradeData != null) {
            this.playerUpgrade.copyFrom(playerUpgradeData.playerUpgrade);
            LOGGER.info("Copied upgrades from PlayerUpgradeData for player: {}", player.getGameProfile().getName());
        } else {
            LOGGER.warn("Attempted to copy from a null PlayerUpgradeData for player: {}", player.getGameProfile().getName());
        }
    }
}