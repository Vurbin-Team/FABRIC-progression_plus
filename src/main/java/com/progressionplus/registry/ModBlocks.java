package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.block.custom.ExperienceStoragePedestal;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final Block DEEPSLATE_TITANIUM_ORE_BLOCK = registerBlock("deepslate_titanium_ore_block",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(30.0F, 1200.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE), UniformIntProvider.create(10,20)));

    public static final Block YELLOW_SKINT_BLOCK = registerBlock("yellow_skint_block",
            new AmethystBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(1.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block TITANIUM_BLOCK = registerBlock("titanium_block",
            new Block(FabricBlockSettings.create()
                    .mapColor(MapColor.WHITE_GRAY)
                    .strength(3f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block YELLOW_SKINT_CRYSTAL = registerBlock("yellow_skint_crystal",
            new AmethystClusterBlock(10, 6, FabricBlockSettings.copy(Blocks.AMETHYST_CLUSTER)
                    .mapColor(MapColor.PURPLE)
                    .strength(1.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .nonOpaque()
                    .luminance((light) -> 5)));

    public static final Block SKINT_BRICKS = registerBlock("skint_bricks",
            new Block(FabricBlockSettings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(2)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block EXPERIENCE_STORAGE_PEDESTAL = registerBlock("experience_storage_pedestal",
            new ExperienceStoragePedestal(FabricBlockSettings.create()
                    .mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(3)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Progressionplus.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(Progressionplus.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void register() {
        Progressionplus.LOGGER.info("Mod Blocks registered successfully!");

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.PROGRESSION_PLUS_GROUP).register((entries) -> {
                    entries.add(ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK);
                    entries.add(ModBlocks.YELLOW_SKINT_BLOCK);
                    entries.add(ModBlocks.YELLOW_SKINT_CRYSTAL);
                    entries.add(ModBlocks.SKINT_BRICKS);
                    entries.add(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL);
                    entries.add(ModBlocks.TITANIUM_BLOCK);
                }
        );
    }
}