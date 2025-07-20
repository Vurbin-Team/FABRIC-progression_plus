package com.progressionplus.network;

import com.progressionplus.Progressionplus;
import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static com.progressionplus.Progressionplus.LOGGER;

public class ClientModMessages {
    public static final Identifier ID = Identifier.of(Progressionplus.MOD_ID, "sync_upgrades");

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            UpgradePayload payload = UpgradePayload.read(buf);
            UpgradeType upgradeType = payload.getUpgradeType();
            int level = payload.getLevel();
            var playerUuid = payload.getPlayerUuid();

            client.execute(() -> {
                var player = client.player;
                if (player != null && player.getUuid().equals(playerUuid)) {
                    var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(client.player);
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
            PacketByteBuf buf = PacketByteBufs.create();
            new UpgradePayload(upgradeType, level, player.getUuid()).write(buf);
            ClientPlayNetworking.send(ID, buf);
        }
    }
}