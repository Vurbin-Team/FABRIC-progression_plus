package com.progressionplus.world.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class StalagmiteFeatureConfig implements FeatureConfig {
    public static final Codec<StalagmiteFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
                    BlockState.CODEC.fieldOf("skintCluster").forGetter(config -> config.skintCluster),
                    Codec.INT.fieldOf("min_height").forGetter(config -> config.minHeight),
                    Codec.INT.fieldOf("max_height").forGetter(config -> config.maxHeight)
            ).apply(instance, StalagmiteFeatureConfig::new)
    );

    public final BlockState block;
    public final BlockState skintCluster;
    public final int minHeight;
    public final int maxHeight;

    public StalagmiteFeatureConfig(BlockState block, BlockState skintCluster, int minHeight, int maxHeight) {
        this.block = block;
        this.skintCluster = skintCluster;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
}