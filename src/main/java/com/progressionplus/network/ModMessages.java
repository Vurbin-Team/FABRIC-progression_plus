

package com.progressionplus.network;

import com.progressionplus.Progressionplus;
import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.util.Identifier;


public class ModMessages {
    public static final int damage = 1;
    public static final double movement_speed = 0.1;
    public static final Identifier ID = Identifier.of(Progressionplus.MOD_ID, "sync_upgrades");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            UpgradePayload payload = UpgradePayload.read(buf);

            server.execute(() -> {
                var upgradeType = payload.getUpgradeType();
                var playerUpgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);

                if (playerUpgradeData.getPlayerUpgrade().tryUpgrade(upgradeType, player)) {
                    int upgradeLevel = playerUpgradeData.getPlayerUpgrade().getLevel(upgradeType);

                    handleUpgradeType(upgradeType, player, upgradeLevel);

                    sendPayload(player, upgradeType, upgradeLevel);
                }
            });
        });
    }

    // Відправляє клієнту пакет з рівнем апгрейду
    public static void sendPayload(ServerPlayerEntity player, UpgradeType type, int level) {
        PacketByteBuf buf = PacketByteBufs.create();
        new UpgradePayload(type, level, player.getUuid()).write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    // Відправляє всі існуючі апгрейди після входу чи респавну
    public static void sendFullSync(ServerPlayerEntity player) {
        var data = PlayerComponents.PLAYER_UPGRADES.get(player).getPlayerUpgrade();
        for (UpgradeType type : UpgradeType.values()) {
            int level = data.getLevel(type);
            if (level > 0) {
                sendPayload(player, type, level);
            }
        }
    }

    // Викликається при вході гравця в світ
    public static void onPlayerJoin(ServerPlayerEntity player) {
        restoreAttributes(player);
        sendFullSync(player);
    }

    // Відновлює атрибути гравця на основі збережених даних
    public static void restoreAttributes(ServerPlayerEntity player) {
        var data = PlayerComponents.PLAYER_UPGRADES.get(player).getPlayerUpgrade();
        for (UpgradeType type : UpgradeType.values()) {
            int level = data.getLevel(type);
            if (level > 0) {
                handleUpgradeType(type, player, level);
            }
        }
    }

    static void handleUpgradeType(UpgradeType upgrade, ServerPlayerEntity player, int level) {
        switch (upgrade) {
            case ENDURANCE -> {
                float bonusHealth = UpgradeConfig.getSettings(UpgradeType.ENDURANCE).bonusPerLevel * level;
                float newMaxHealth = 20 + bonusHealth;

                var healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(newMaxHealth);
                    // Восстанавливаем здоровье до максимума только если текущее здоровье меньше нового максимума
                    if (player.getHealth() < newMaxHealth) {
                        player.setHealth(newMaxHealth);
                    }
                }
            }
            case STRENGTH -> {
                float damageBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.STRENGTH).bonusPerLevel * level;
                double bonusDamage = damageBonusPerLevel * damage;

                var damageAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (damageAttribute != null) {
                    damageAttribute.setBaseValue(damage + bonusDamage);
                }
            }
            case AGILITY -> {
                float speedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.AGILITY).bonusPerLevel * level;
                double bonusSpeed = speedBonusPerLevel * movement_speed;

                var speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (speedAttribute != null) {
                    speedAttribute.setBaseValue(movement_speed + bonusSpeed);
                }
            }
            case LUCK -> {
                // Handle LUCK upgrade
                double bonusLuck = UpgradeConfig.getSettings(UpgradeType.LUCK).bonusPerLevel * level;

                var luckAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_LUCK);
                if (luckAttribute != null) {
                    luckAttribute.setBaseValue(bonusLuck);
                }
            }
            case MINING_SPEED -> {

            }
        }
    }
}
