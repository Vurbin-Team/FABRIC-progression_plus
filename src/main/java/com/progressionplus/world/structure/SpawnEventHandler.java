package com.progressionplus.world.structure;

import com.progressionplus.Progressionplus;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class SpawnEventHandler {
    private static final String MARKER_FILE = "spawn_structure_generated.marker";

    // Блоки, которые считаются подходящими для размещения структуры
    private static final Set<Block> SURFACE_BLOCKS = Set.of(
            Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL,
            Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.STONE,
            Blocks.DEEPSLATE, Blocks.GRAVEL, Blocks.CLAY
    );

    // Блоки, которые можно заменить при подготовке местности
    private static final Set<Block> CLEARABLE_BLOCKS = Set.of(
            Blocks.GRASS_BLOCK, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN,
            Blocks.DEAD_BUSH, Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID,
            Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
            Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY,
            Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.SNOW,
            Blocks.VINE, Blocks.SWEET_BERRY_BUSH
    );

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(SpawnEventHandler::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) return;

        Path worldDir = server.getSavePath(net.minecraft.util.WorldSavePath.ROOT);
        Path markerFile = worldDir.resolve(MARKER_FILE);

        if (Files.exists(markerFile)) {
            Progressionplus.LOGGER.info("Spawn structure already generated, skipping");
            return;
        }

        if (generateSpawnStructure(world)) {
            try {
                Files.createFile(markerFile);
                Progressionplus.LOGGER.info("Created spawn structure marker file");
            } catch (IOException e) {
                Progressionplus.LOGGER.error("Failed to create marker file", e);
            }
        }
    }

    private static boolean generateSpawnStructure(ServerWorld world) {
        try {
            InputStream inputStream = SpawnEventHandler.class.getResourceAsStream("/data/" + Progressionplus.MOD_ID + "/structures/spawn_structure.nbt");
            if (inputStream == null) {
                Progressionplus.LOGGER.error("Could not find spawn_structure.nbt file in resources!");
                return false;
            }

            NbtCompound nbt = readNbtFile(inputStream);
            if (nbt == null) {
                Progressionplus.LOGGER.error("Failed to read NBT data from structure file");
                return false;
            }

            StructureTemplate template = new StructureTemplate();
            template.readNbt(world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK), nbt);

            int structureWidth = 21;
            int structureHeight = 11;
            int offset = structureWidth / 2;

            BlockPos spawnPos = world.getSpawnPos();

            // Найти лучшую позицию для размещения структуры
            BlockPos bestPlacementPos = findBestPlacementPos(world, spawnPos, structureWidth, offset);

            if (bestPlacementPos == null) {
                Progressionplus.LOGGER.error("Could not find suitable placement position");
                return false;
            }

            // Подготовить местность для структуры
//            prepareTerrain(world, bestPlacementPos, structureWidth, structureHeight);

            StructurePlacementData placementData = new StructurePlacementData()
                    .setRotation(BlockRotation.NONE)
                    .setMirror(BlockMirror.NONE)
                    .setPosition(bestPlacementPos)
                    .setUpdateNeighbors(true)
                    .setIgnoreEntities(false)
                    .setLiquidSettings(StructureLiquidSettings.APPLY_WATERLOGGING)
                    .setInitializeMobs(true)
                    .setRandom(world.getRandom()).addProcessor(new TerrainMatchingProcessor(world));

            boolean success = template.place(world, bestPlacementPos, bestPlacementPos, placementData, world.getRandom(), 2);

            if (success) {
                // Интегрировать структуру с ландшафтом
//                integrateWithLandscape(world, bestPlacementPos, structureWidth, structureHeight);

                // Установить спавн игрока
                BlockPos playerSpawn = new BlockPos(bestPlacementPos.getX() + offset, bestPlacementPos.getY() + 2, bestPlacementPos.getZ() + offset);
                world.setSpawnPos(playerSpawn, 0);

                Progressionplus.LOGGER.info("Successfully placed spawn structure at " + bestPlacementPos);
                return true;
            } else {
                Progressionplus.LOGGER.error("Failed to place spawn structure");
                return false;
            }

        } catch (Exception e) {
            Progressionplus.LOGGER.error("Unexpected error while generating spawn structure: ", e);
            return false;
        }
    }

    /**
     * Найти лучшую позицию для размещения структуры, избегая деревьев и неровностей
     */
    private static BlockPos findBestPlacementPos(ServerWorld world, BlockPos center, int structureWidth, int offset) {
        int searchRadius = 48; // Увеличиваем радиус поиска
        BlockPos bestPos = null;
        int bestScore = Integer.MIN_VALUE;

        // Проверяем несколько позиций вокруг спавна
        for (int attempt = 0; attempt < 20; attempt++) {
            int x = center.getX() + world.getRandom().nextInt(searchRadius * 2) - searchRadius;
            int z = center.getZ() + world.getRandom().nextInt(searchRadius * 2) - searchRadius;

            // Используем MOTION_BLOCKING_NO_LEAVES чтобы игнорировать листву
            int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos testPos = new BlockPos(x - offset, surfaceY, z - offset);

            int score = evaluatePosition(world, testPos, structureWidth);
            if (score > bestScore) {
                bestScore = score;
                bestPos = testPos;
            }
        }

        // Если не нашли хорошее место, используем позицию рядом со спавном
        if (bestPos == null) {
            int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, center.getX(), center.getZ());
            bestPos = new BlockPos(center.getX() - offset, surfaceY, center.getZ() - offset);
        }

        return bestPos;
    }

    /**
     * Оценить позицию для размещения структуры
     */
    private static int evaluatePosition(ServerWorld world, BlockPos pos, int structureWidth) {
        int score = 0;
        int surfaceBlocks = 0;
        int obstacles = 0;
        int heightVariation = 0;

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Проверяем область структуры
        for (int x = 0; x < structureWidth; x += 2) {
            for (int z = 0; z < structureWidth; z += 2) {
                BlockPos checkPos = pos.add(x, 0, z);

                // Проверяем высоту поверхности
                int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, checkPos.getX(), checkPos.getZ());
                minY = Math.min(minY, surfaceY);
                maxY = Math.max(maxY, surfaceY);

                // Проверяем блок поверхности
                BlockState surfaceBlock = world.getBlockState(new BlockPos(checkPos.getX(), surfaceY - 1, checkPos.getZ()));
                if (SURFACE_BLOCKS.contains(surfaceBlock.getBlock())) {
                    surfaceBlocks++;
                }

                // Проверяем препятствия (деревья, структуры)
                for (int y = surfaceY; y < surfaceY + 10; y++) {
                    BlockState blockState = world.getBlockState(new BlockPos(checkPos.getX(), y, checkPos.getZ()));
                    if (!blockState.isAir() && !CLEARABLE_BLOCKS.contains(blockState.getBlock())) {
                        obstacles++;
                        break; // Достаточно одного препятствия в столбце
                    }
                }
            }
        }

        heightVariation = maxY - minY;

        // Рассчитываем итоговый балл
        score += surfaceBlocks * 3;      // Бонус за подходящие блоки поверхности
        score -= obstacles * 10;         // Большой штраф за препятствия (деревья)
        score -= heightVariation * 2;    // Штраф за неровность местности

        // Бонус за ровную местность
        if (heightVariation <= 2) {
            score += 20;
        }

        return score;
    }

    private static NbtCompound readNbtFile(InputStream inputStream) {
        NbtSizeTracker sizeTracker = NbtSizeTracker.ofUnlimitedBytes();

        try {
            Progressionplus.LOGGER.info("Trying to read NBT as compressed format...");
            return NbtIo.readCompressed(inputStream, sizeTracker);
        } catch (Exception e) {
            Progressionplus.LOGGER.info("Compressed format failed: " + e.getMessage());
        }

        try {
            inputStream.close();
            inputStream = SpawnEventHandler.class.getResourceAsStream("/data/" + Progressionplus.MOD_ID + "/structures/spawn_structure.nbt");

            Progressionplus.LOGGER.info("Trying to read NBT as uncompressed format...");
            DataInputStream dataStream = new DataInputStream(inputStream);
            NbtElement element = NbtIo.read(dataStream, sizeTracker);

            if (element instanceof NbtCompound compound) {
                return compound;
            } else {
                Progressionplus.LOGGER.error("NBT root element is not a compound tag, got: " + element.getClass().getSimpleName());
                return null;
            }
        } catch (Exception e) {
            Progressionplus.LOGGER.error("Uncompressed format also failed: " + e.getMessage());
        }

        Progressionplus.LOGGER.error("All NBT reading methods failed!");
        return null;
    }
}