package com.progressionplus.registry.block;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.block.entity.ExperienceStoragePedestalEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ExperienceStoragePedestalEntity> EXPERIENCE_PEDESTAL_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Progressionplus.MOD_ID, "experience_pedestal"),
                    FabricBlockEntityTypeBuilder.create(
                            ExperienceStoragePedestalEntity::new,
                            ModBlocks.EXPERIENCE_STORAGE_PEDESTAL // Ваш блок
                    ).build()
            );

    public static void registerBlockEntities() {
        Progressionplus.LOGGER.info("Registering Block Entities for " + Progressionplus.MOD_ID);
    }
}