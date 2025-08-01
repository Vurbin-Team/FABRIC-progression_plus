package com.progressionplus.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void enforceExactSpawnOnConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ServerWorld world = player.getServerWorld();
        BlockPos worldSpawn = world.getSpawnPos();

        // Гарантируем точную позицию при подключении
        player.refreshPositionAndAngles(
                worldSpawn.getX() + 0.5,
                worldSpawn.getY(),
                worldSpawn.getZ() + 0.5,
                0.0f,
                0.0f
        );

        // Синхронизируем позицию с клиентом
        player.networkHandler.syncWithPlayerPosition();
    }
}