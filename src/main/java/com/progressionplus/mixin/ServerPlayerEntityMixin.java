package com.progressionplus.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    // Перехватываем метод moveToSpawn для установки точной позиции при создании игрока
    @Redirect(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getSpawnRadius(Lnet/minecraft/server/world/ServerWorld;)I"))
    private int forceZeroSpawnRadius(net.minecraft.server.MinecraftServer server, ServerWorld world) {
        // Возвращаем 0, чтобы отключить радиус спавна
        return 0;
    }

    // Альтернативный подход - перехватываем установку позиции в moveToSpawn
    @Inject(method = "moveToSpawn", at = @At("TAIL"))
    private void setExactSpawnPosition(ServerWorld world, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        BlockPos worldSpawn = world.getSpawnPos();

        // Принудительно устанавливаем точную позицию спавна
        player.refreshPositionAndAngles(
                worldSpawn.getX() + 0.5,
                worldSpawn.getY(),
                worldSpawn.getZ() + 0.5,
                world.getSpawnAngle(),
                0.0f
        );
    }

    // Перехватываем метод copyFrom для респавна после смерти
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void setExactSpawnOnRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (!alive) { // Только при респавне после смерти
            ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
            ServerWorld world = player.getServerWorld();

            // Если у игрока нет кастомной точки спавна, используем мировую
            if (player.getSpawnPointPosition() == null) {
                BlockPos worldSpawn = world.getSpawnPos();
                player.refreshPositionAndAngles(
                        worldSpawn.getX() + 0.5,
                        worldSpawn.getY(),
                        worldSpawn.getZ() + 0.5,
                        world.getSpawnAngle(),
                        0.0f
                );
            }
        }
    }
}