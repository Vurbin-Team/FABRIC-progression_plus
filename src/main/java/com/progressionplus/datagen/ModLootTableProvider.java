package com.progressionplus.datagen;

import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK, oreDrops(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK, ModItems.RAW_TITANIUM_ORE));
        addDrop(ModBlocks.YELLOW_SKINT_BLOCK, drops(ModBlocks.YELLOW_SKINT_BLOCK));
        addDrop(ModBlocks.SKINT_BRICKS, drops(ModBlocks.SKINT_BRICKS));
        addDrop(ModBlocks.YELLOW_SKINT_CRYSTAL, multipleOreDrops(ModBlocks.YELLOW_SKINT_CRYSTAL, ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2, 1, 4));
        addDrop(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL, drops(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL));
        addDrop(ModBlocks.TITANIUM_BLOCK, drops(ModBlocks.TITANIUM_BLOCK));
    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }
}