package com.progressionplus;

import com.progressionplus.attributes.ModAttributes;
import com.progressionplus.config.UpgradeConfigLoader;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.network.ModMessages;
import com.progressionplus.network.ServerDimensionSwitch;
import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.ModItemGroups;
import com.progressionplus.registry.ModItems;
import com.progressionplus.registry.block.ModBlockEntities;
import com.progressionplus.sounds.ModSounds;
import com.progressionplus.util.ModFeatures;
import com.progressionplus.world.ModPlacedFeatures;
import com.progressionplus.world.gen.ModWorldGeneration;
import com.progressionplus.world.structure.SpawnEventHandler;
import net.fabricmc.api.ModInitializer;

import com.progressionplus.config.UpgradeConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Progressionplus implements ModInitializer {
	public static final String MOD_ID = "progression-plus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Progression+");

		// Инициализация конфигурации
		UpgradeConfig.init();
		UpgradeConfigLoader.load();
		ModMessages.init();
		ModSounds.registerSounds();

		ModBlocks.register();
		ModItems.registerModItems();
		ModItemGroups.register();
		ModWorldGeneration.generateModWorldGen();
		SpawnEventHandler.initialize();
		ModBlockEntities.registerBlockEntities();
		ModFeatures.registerFeatures();

		BiomeModifications.addFeature(
				BiomeSelectors.foundInOverworld(),
				GenerationStep.Feature.UNDERGROUND_DECORATION,
				ModPlacedFeatures.SKINT_STALAGMITE_PLACED_KEY
		);

		// Add player join/leave handlers
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			ModMessages.onPlayerJoin(player);
		});

		ServerEntityEvents.ENTITY_LOAD.register(ServerDimensionSwitch::Register);
	}
}