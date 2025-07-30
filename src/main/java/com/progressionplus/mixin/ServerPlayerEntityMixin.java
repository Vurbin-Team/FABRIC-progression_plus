package com.progressionplus.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    // Перехватываем конструктор для установки точной позиции при создании игрока
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setExactSpawnOnCreate(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        ServerWorld world = player.getServerWorld();
        BlockPos worldSpawn = world.getSpawnPos();

        // Устанавливаем точную позицию спавна вместо случайной
        player.refreshPositionAndAngles(
                worldSpawn.getX() + 0.5,
                worldSpawn.getY(),
                worldSpawn.getZ() + 0.5,
                0.0f, // всегда смотрим на север
                0.0f
        );
    }

    // Перехватываем метод copyFrom для респавна после смерти
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void setExactSpawnOnRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        ServerWorld world = player.getServerWorld();
        BlockPos worldSpawn = world.getSpawnPos();

        // Принудительно устанавливаем точную позицию спавна
        player.refreshPositionAndAngles(
                worldSpawn.getX() + 0.5,
                worldSpawn.getY(),
                worldSpawn.getZ() + 0.5,
                0.0f,
                0.0f
        );
    }

    @Inject(method = "getWorldSpawnPos", at = @At("HEAD"), cancellable = true)
    private void forceExactWorldSpawn(ServerWorld world, BlockPos basePos, CallbackInfoReturnable<BlockPos> cir) {
        // Всегда возвращаем точную позицию спавна мира, игнорируя радиус спавна
        cir.setReturnValue(world.getSpawnPos());
    }
}