package com.progressionplus.datagen;

import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.ModItems;
import com.progressionplus.registry.armor.CustomEquipmentAssetKeys;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.client.data.*;
import net.minecraft.client.render.RenderLayer;

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

        itemModelGenerator.registerArmor(ModItems.TITANIUM_HELMET, CustomEquipmentAssetKeys.TITANIUM, ItemModelGenerator.HELMET_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_CHESTPLATE, CustomEquipmentAssetKeys.TITANIUM, ItemModelGenerator.CHESTPLATE_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_LEGGINGS, CustomEquipmentAssetKeys.TITANIUM, ItemModelGenerator.LEGGINGS_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(ModItems.TITANIUM_BOOTS, CustomEquipmentAssetKeys.TITANIUM, ItemModelGenerator.BOOTS_TRIM_ID_PREFIX, false);
    }
}