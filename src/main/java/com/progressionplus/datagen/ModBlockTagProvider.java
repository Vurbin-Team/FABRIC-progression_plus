package com.progressionplus.datagen;

import com.progressionplus.registry.ModBlocks;
import com.progressionplus.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK)
                .add(ModBlocks.YELLOW_SKINT_BLOCK)
                .add(ModBlocks.YELLOW_SKINT_CRYSTAL)
                .add(ModBlocks.SKINT_BRICKS)
                .add(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL)
                .add(ModBlocks.TITANIUM_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK)
                .add(ModBlocks.TITANIUM_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.YELLOW_SKINT_BLOCK)
                .add(ModBlocks.YELLOW_SKINT_CRYSTAL)
                .add(ModBlocks.SKINT_BRICKS)
                .addTag(ModTags.Blocks.NEEDS_TITANIUM_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL)
                .add(ModBlocks.TITANIUM_BLOCK);

        getOrCreateTagBuilder(ModTags.Blocks.NEEDS_TITANIUM_TOOL)
                .add(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK)
                .add(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL)
                .add(ModBlocks.TITANIUM_BLOCK);
    }
}