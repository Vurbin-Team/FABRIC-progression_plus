package com.progressionplus.world;

import com.progressionplus.Progressionplus;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> DEEPSLATE_TITANIUM_ORE_PLACED_KEY = registerKey("deepslate_titanium_ore_placed");
    public static final RegistryKey<PlacedFeature> SPAWN_STRUCTURE_PLACED_KEY = registerKey("spawn_structure_placed");
    public static final RegistryKey<PlacedFeature> SKINT_STALAGMITE_PLACED_KEY = registerKey("skint_stalagmite_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, DEEPSLATE_TITANIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_KEY),
                ModOrePlacement.modifiersWithCount(3,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(80))));

        register(context, SPAWN_STRUCTURE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SPAWN_STRUCTURE_KEY),
                ModOrePlacement.modifiersWithRarity(1000, // очень редко, так как нужна только одна
                        HeightRangePlacementModifier.uniform(YOffset.fixed(40), YOffset.fixed(80))));

        register(context, SKINT_STALAGMITE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SKINT_STALAGMITE_KEY),
                List.of(
                        RarityFilterPlacementModifier.of(6), // 1 попытки на чанк (достаточно для редкой генерации)
                        SquarePlacementModifier.of(), // случайное размещение в квадрате чанка
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-60), YOffset.fixed(20)), // подземная генерация
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, // сканируем вниз
                                BlockPredicate.matchingBlocks(Blocks.DEEPSLATE), // ищем deepslate
                                32), // максимум 32 блока для поиска
                        BiomePlacementModifier.of() // размещаем только в подходящих биомах
                ));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Progressionplus.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}