package com.progressionplus.util;

import com.progressionplus.Progressionplus;
import com.progressionplus.world.config.StalagmiteFeatureConfig;
import com.progressionplus.world.feature.StalagmiteFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<StalagmiteFeatureConfig> STALAGMITE_FEATURE =
            Registry.register(Registries.FEATURE,
                    Identifier.of(Progressionplus.MOD_ID, "stalagmite"),
                    new StalagmiteFeature(StalagmiteFeatureConfig.CODEC));

    public static void registerFeatures() {
        // Feature уже зарегистрирована выше, этот метод можно вызвать в main классе для инициализации
    }
}
