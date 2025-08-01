package com.progressionplus.registry.items;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class GrandExpBottleItem extends Item {
    public GrandExpBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32; // Стандартное время для питья зелий
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand); // Важно! Это запускает анимацию питья
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (!world.isClient) {
                // Добавляем опыт
                player.addExperience(1500);

                // Увеличиваем статистику использования предмета
                player.incrementStat(Stats.USED.getOrCreateStat(this));

                // Триггер для достижений (если нужно)
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                }
            }

            // Воспроизводим звук питья
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Уменьшаем количество предметов и возвращаем пустую бутылку
            if (!player.isCreative()) {
                stack.decrement(1);

                // Добавляем пустую бутылку в инвентарь (как у зелий)
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().insertStack(emptyBottle)) {
                    player.dropItem(emptyBottle, false);
                }
            }
        }

        return stack;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Добавляет магическое свечение как у зачарованных предметов
    }
}