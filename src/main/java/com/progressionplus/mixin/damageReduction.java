package com.progressionplus.mixin;

import com.progressionplus.playerResistances.DamageResistanceHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class damageReduction {

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float modifyDamageAmount(float amount, DamageSource source) {
        if (!((Object) this instanceof ServerPlayerEntity serverPlayer)) {
            return amount;
        }

        float reduced = DamageResistanceHandler.calculateDamageReduction(serverPlayer, source, amount);

        return Math.max(0.0F, reduced);
    }
}