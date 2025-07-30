package com.progressionplus.registry;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.block.custom.ExperienceStoragePedestal;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.function.Function;

public class ModBlocks {
    public static final Block DEEPSLATE_TITANIUM_ORE_BLOCK = registerBlock("deepslate_titanium_ore_block",
            (settings) -> new ExperienceDroppingBlock(UniformIntProvider.create(10,20), settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(30.0F, 1200.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE));

    public static final Block YELLOW_SKINT_BLOCK = registerBlock("yellow_skint_block", AmethystBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(1.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK));

    public static final Block TITANIUM_BLOCK = registerBlock("titanium_block", Block::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.WHITE_GRAY)
                    .strength(3f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.IRON));

    public static final Block YELLOW_SKINT_CRYSTAL = registerBlock(
            "yellow_skint_crystal",
            (settings) -> new AmethystClusterBlock(10,6, settings),
            AbstractBlock.Settings.copyShallow(Blocks.AMETHYST_CLUSTER)
                    .mapColor(MapColor.PURPLE)
                    .strength(1.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .nonOpaque()
                    .luminance( light -> 7));

    public static final Block SKINT_BRICKS = registerBlock("skint_bricks", Block::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(2)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK));

    public static final Block EXPERIENCE_STORAGE_PEDESTAL = registerBlock("experience_storage_pedestal", ExperienceStoragePedestal::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(3)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE_BRICKS));


    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Progressionplus.MOD_ID, name));
        Block block = factory.apply(settings.registryKey(key));
        registerBlockItem(name, block);

        return Registry.register(Registries.BLOCK, key, block);
    }

    private static void registerBlockItem(String name, Block block) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Progressionplus.MOD_ID, name));
        BlockItem item = new BlockItem(block, new Item.Settings().registryKey(key));
        Registry.register(Registries.ITEM, key, item);
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
