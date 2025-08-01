package com.progressionplus.mixin.client;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.items.GrandExpBottleItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getCustomModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof GrandExpBottleItem) {
            BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();

            // Проверяем контекст - если в руках, используем 3D модель
            if (entity != null && (stack == entity.getMainHandStack() || stack == entity.getOffHandStack())) {
                // Для модели в руках
                ModelIdentifier handModelId = new ModelIdentifier(
                        Progressionplus.MOD_ID,
                        "grand_exp_bottle_3d",
                        "inventory"
                );
                BakedModel handModel = modelManager.getModel(handModelId);
                if (handModel != null) {
                    cir.setReturnValue(handModel);
                }
            } else {
                // В инвентаре используем 2D модель
                ModelIdentifier inventoryModelId = new ModelIdentifier(
                        Progressionplus.MOD_ID,
                        "grand_exp_bottle",
                        "inventory"
                );
                BakedModel inventoryModel = modelManager.getModel(inventoryModelId);
                if (inventoryModel != null) {
                    cir.setReturnValue(inventoryModel);
                }
            }
        }
    }
}