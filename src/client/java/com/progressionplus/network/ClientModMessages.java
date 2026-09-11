package com.progressionplus.network;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.UUID;

import static com.progressionplus.Progressionplus.LOGGER;

public class ClientModMessages {
    public static void initClient() {
        System.out.println("Initializing client-side networking...");

        ClientPlayNetworking.registerGlobalReceiver(UpgradePayload.ID, (payload, context) -> {
            UpgradeType upgradeType = payload.upgradeType();
            int level = payload.level();
            UUID playerUuid = payload.playerUuid();

            context.client().execute(() -> {
                if (context.client().player != null && context.client().player.getUuid().equals(playerUuid)) {
                    var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(context.client().player);
                    var upgrades = upgradeData.getPlayerUpgrade().getUpgrades();
                    upgrades.put(upgradeType.name(), level);
                    upgradeData.getPlayerUpgrade().loadUpgrades(upgrades);
                }
            });
        });

        LOGGER.info("Client-side networking initialized successfully.");
    }

    public static void sendSyncPacketToServer(UpgradeType upgradeType, ClientPlayerEntity player) {
        if (player != null) {
            var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
            int level = upgradeData.getPlayerUpgrade().getLevel(upgradeType);
            ClientPlayNetworking.send(new UpgradePayload(upgradeType, level, player.getUuid()));
        }
    }
}