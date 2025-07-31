package com.progressionplus.world;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.ModBlocks;
import com.progressionplus.util.ModFeatures;
import com.progressionplus.world.config.StalagmiteFeatureConfig;
import com.progressionplus.world.feature.StalagmiteFeature;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> DEEPSLATE_TITANIUM_ORE_KEY = registerKey("deepslate_titanium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SPAWN_STRUCTURE_KEY = registerKey("spawn_structure");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SKINT_STALAGMITE_KEY = registerKey("skint_stalagmite");

    public static void bootstrap(Registerable<ConfiguredFeature<?,?>> context) {
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldTitaniumOres = List.of(
                OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_TITANIUM_ORE_BLOCK.getDefaultState())
        );

        register(context, DEEPSLATE_TITANIUM_ORE_KEY,
                Feature.ORE, new OreFeatureConfig(overworldTitaniumOres, 7));

        register(context, SPAWN_STRUCTURE_KEY,
                Feature.NO_OP, FeatureConfig.DEFAULT);

        register(context, SKINT_STALAGMITE_KEY,
                ModFeatures.STALAGMITE_FEATURE,
                new StalagmiteFeatureConfig(
                        ModBlocks.YELLOW_SKINT_BLOCK.getDefaultState(), // убедитесь что этот блок существует
                        ModBlocks.YELLOW_SKINT_CRYSTAL.getDefaultState(),
                        5, 11
                ));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Progressionplus.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
