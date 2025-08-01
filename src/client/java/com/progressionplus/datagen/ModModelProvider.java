package com.progressionplus.datagen;

import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.YELLOW_SKINT_BLOCK);
        blockStateModelGenerator.registerAmethyst(ModBlocks.YELLOW_SKINT_CRYSTAL);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SKINT_BRICKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.TITANIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_TITANIUM_ORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEEPSLATE_HANDLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2, Models.GENERATED);
        itemModelGenerator.register(ModBlocks.YELLOW_SKINT_CRYSTAL.asItem(), Models.GENERATED);

        itemModelGenerator.register(ModItems.TITANIUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_HOE, Models.HANDHELD);

        itemModelGenerator.registerArmor(ModItems.TITANIUM_HELMET);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_CHESTPLATE);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_LEGGINGS);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_BOOTS);
    }
}