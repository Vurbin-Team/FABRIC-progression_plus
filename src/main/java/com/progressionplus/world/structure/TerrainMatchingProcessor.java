package com.progressionplus.world.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.Heightmap;

import java.util.Set;

/**
 * Кастомный процессор, имитирующий поведение jigsaw "terrain_matching" projection
 */
public class TerrainMatchingProcessor extends StructureProcessor {

    private final ServerWorld world;

    // Блоки, которые считаются "основанием" структуры
    private static final Set<Block> FOUNDATION_BLOCKS = Set.of(
            Blocks.STONE_BRICKS, Blocks.COBBLESTONE, Blocks.STONE,
            Blocks.DEEPSLATE, Blocks.DIRT, Blocks.GRASS_BLOCK
    );

    // Блоки, которые должны адаптироваться к местности
    private static final Set<Block> TERRAIN_ADAPTIVE_BLOCKS = Set.of(
            Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COBBLESTONE, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_BLOCK,
            Blocks.RED_SAND, Blocks.DEEPSLATE, Blocks.MUD, Blocks.PODZOL, Blocks.COARSE_DIRT,
            Blocks.GRAVEL, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA,
            Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
            Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA,
            Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
            Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA,
            Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA
    );

    public TerrainMatchingProcessor(ServerWorld world) {
        this.world = world;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView worldView,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData data) {

        BlockState blockState = currentBlockInfo.state();
        BlockPos blockPos = currentBlockInfo.pos();

        // Проверяем, нужно ли адаптировать этот блок
        if (!TERRAIN_ADAPTIVE_BLOCKS.contains(blockState.getBlock())) {
            return currentBlockInfo;
        }

        // Получаем высоту местности в этой позиции
        int terrainHeight = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ());
        int blockY = blockPos.getY();

        // Если блок ниже уровня местности, заменяем на подходящий материал
        if (blockY < terrainHeight) {
            BlockState terrainBlock = world.getBlockState(new BlockPos(blockPos.getX(), terrainHeight - 1, blockPos.getZ()));

            // Используем блок местности или подходящую замену
            BlockState replacementState = getAppropriateTerrainBlock(terrainBlock, blockState);

            return new StructureTemplate.StructureBlockInfo(
                    blockPos,
                    replacementState,
                    currentBlockInfo.nbt()
            );
        }

        // Если блок является основанием и находится над землей, добавляем поддержку
        if (FOUNDATION_BLOCKS.contains(blockState.getBlock()) && blockY > terrainHeight) {
            // Заполняем пространство под блоком до земли
            fillSupportColumn(blockPos, terrainHeight);
        }

        return currentBlockInfo;
    }

    /**
     * Определяет подходящий блок местности для замены
     */
    private BlockState getAppropriateTerrainBlock(BlockState terrainBlock, BlockState originalBlock) {
        Block terrain = terrainBlock.getBlock();

        // Если местность - трава, используем траву
        if (terrain == Blocks.GRASS_BLOCK) {
            return Blocks.GRASS_BLOCK.getDefaultState();
        }

        // Если местность - песок, используем песок
        if (terrain == Blocks.SAND || terrain == Blocks.RED_SAND) {
            return terrain == Blocks.RED_SAND ? Blocks.RED_SAND.getDefaultState() : Blocks.SAND.getDefaultState();
        }

        // Если местность - камень, используем камень
        if (terrain == Blocks.STONE || terrain == Blocks.DEEPSLATE) {
            return terrain.getDefaultState();
        }

        // По умолчанию используем dirt
        return Blocks.DIRT.getDefaultState();
    }

    /**
     * Заполняет колонну поддержки под висящим блоком
     */
    private void fillSupportColumn(BlockPos startPos, int terrainHeight) {
        for (int y = startPos.getY() - 1; y >= terrainHeight; y--) {
            BlockPos fillPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState currentState = world.getBlockState(fillPos);

            // Заполняем только воздух и жидкости
            if (currentState.isAir() || currentState.getBlock() == Blocks.WATER || currentState.getBlock() == Blocks.LAVA) {
                // Определяем подходящий блок для заполнения
                BlockState fillBlock = y == terrainHeight ?
                        getTerrainSurfaceBlock(fillPos) :
                        Blocks.STONE_BRICKS.getDefaultState();

                world.setBlockState(fillPos, fillBlock);
            }
        }
    }

    /**
     * Определяет подходящий поверхностный блок для позиции
     */
    private BlockState getTerrainSurfaceBlock(BlockPos pos) {
        // Проверяем биом для определения подходящего блока
        var biome = world.getBiome(pos).value();
        float temperature = biome.getTemperature();

        if (temperature > 1.0f) {
            return Blocks.SAND.getDefaultState(); // Пустынные биомы
        } else if (temperature < 0.0f) {
            return Blocks.DIRT.getDefaultState(); // Холодные биомы
        } else {
            return Blocks.GRASS_BLOCK.getDefaultState(); // Умеренные биомы
        }
    }

    @Override
    protected StructureProcessorType<?> getType() {
        // Возвращаем null так как это кастомный процессор только для рантайма
        return null;
    }
}