package com.progressionplus.datagen;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementRequirements requirements = AdvancementRequirements.anyOf(
                List.of(new String[]{
                        "has_grand_exp_bottle",
                        "has_glow_ink_sac",
                        "has_echo_shard",
                        "has_nether_wart",
                        "has_amethyst_shard",
                        "has_glass_bottle",
                        "has_experience_bottle"
                })
        );

        // Создаем RegistryKey для рецепта
        RegistryKey<Recipe<?>> grandExpBottleRecipeKey = RegistryKey.of(
                RegistryKeys.RECIPE,
                Identifier.of(Progressionplus.MOD_ID, "grand_exp_bottle")
        );

        // Advancement для разблокировки рецепта Grand Exp Bottle
        AdvancementEntry grandExpBottleRecipe = Advancement.Builder.create()
                .display(
                        ModItems.GRAND_EXP_BOTTLE,
                        Text.translatable("advancement.progression-plus.grand_exp_bottle_recipe.title"),
                        Text.translatable("advancement.progression-plus.grand_exp_bottle_recipe.description"),
                        null, // background
                        AdvancementFrame.TASK,
                        true, // showToast
                        true, // announceToChat
                        false // hidden
                )
                .criterion("has_grand_exp_bottle", InventoryChangedCriterion.Conditions.items(ModItems.GRAND_EXP_BOTTLE))
                .criterion("has_glow_ink_sac", InventoryChangedCriterion.Conditions.items(Items.GLOW_INK_SAC))
                .criterion("has_echo_shard", InventoryChangedCriterion.Conditions.items(Items.ECHO_SHARD))
                .criterion("has_nether_wart", InventoryChangedCriterion.Conditions.items(Items.NETHER_WART))
                .criterion("has_amethyst_shard", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                .criterion("has_glass_bottle", InventoryChangedCriterion.Conditions.items(Items.GLASS_BOTTLE))
                .criterion("has_experience_bottle", InventoryChangedCriterion.Conditions.items(Items.EXPERIENCE_BOTTLE))
                .rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(Identifier.of("grand_exp_bottle"))).requirements(
                        requirements
                )
                .build(consumer, "grand_exp_bottle");
    }
}
