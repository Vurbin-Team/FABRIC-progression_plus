package com.progressionplus.mixin;

import com.progressionplus.registry.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void injectCustomCraft(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;

        // Получаем предметы из слотов
        ItemStack leftItem = handler.getSlot(0).getStack();
        ItemStack rightItem = handler.getSlot(1).getStack();
        ItemStack resultSlot = handler.getSlot(2).getStack();

        // Проверяем наш кастомный рецепт
        if (isCustomRecipe(leftItem, rightItem)) {
            // Создаем результат
            ItemStack result = new ItemStack(ModItems.CHARGED_TITANIUM_SWORD);

            // Устанавливаем результат в слот
            handler.getSlot(2).setStack(result);

            // Устанавливаем стоимость в уровнях опыта
            this.levelCost.set(25);

            // Отменяем стандартную логику
            ci.cancel();
        }
    }

    private boolean isCustomRecipe(ItemStack left, ItemStack right) {
        // Пример: меч + алмаз = улучшенный меч
        return left.getItem() == ModItems.TITANIUM_SWORD &&
                right.getItem() == ModItems.SKINT_CORE;
    }
}