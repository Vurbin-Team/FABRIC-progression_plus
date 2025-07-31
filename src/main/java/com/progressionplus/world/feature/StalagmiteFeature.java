package com.progressionplus.world.feature;

import com.mojang.serialization.Codec;
import com.progressionplus.world.config.StalagmiteFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class StalagmiteFeature extends Feature<StalagmiteFeatureConfig> {

    public StalagmiteFeature(Codec<StalagmiteFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<StalagmiteFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();
        StalagmiteFeatureConfig config = context.getConfig();

        // Ищем подходящую поверхность для генерации
        BlockPos groundPos = findGroundPos(world, pos);
        if (groundPos == null) {
            return false;
        }

        // Проверяем, что под нами deepslate
        if (!world.getBlockState(groundPos).isOf(Blocks.DEEPSLATE)) {
            return false;
        }

        // Проверяем, что над поверхностью есть место для генерации (минимум 3 блока воздуха)
        BlockPos airCheckPos = groundPos.up();
        int airBlocks = 0;
        for (int i = 0; i < 10; i++) {
            if (world.getBlockState(airCheckPos.up(i)).isAir()) {
                airBlocks++;
            } else {
                break;
            }
        }

        if (airBlocks < 3) {
            return false;
        }

        // Определяем тип и размер сталагмита
        StalagmiteType type = StalagmiteType.getRandom(random);
        int mainHeight = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight;
        mainHeight = Math.min(mainHeight, type.getMaxHeight());
        mainHeight = Math.min(mainHeight, airBlocks); // Не превышаем доступное пространство

        // Генерируем основной столб в центре
        generateMainColumn(world, groundPos.up(), config.block, config.skintCluster, mainHeight, random);

        // Генерируем дополнительные колонки в формате 3x3 (без центра)
        generate3x3Columns(world, groundPos.up(), config.block, config.skintCluster, mainHeight, random);

        return true;
    }

    private BlockPos findGroundPos(StructureWorldAccess world, BlockPos startPos) {
        BlockPos.Mutable mutable = startPos.mutableCopy();

        // Сначала идем вниз, пока не найдем твердый блок
        for (int i = 0; i < 64; i++) {
            BlockState state = world.getBlockState(mutable);
            if (state.isSolidBlock(world, mutable) && !state.isAir()) {
                return mutable.toImmutable();
            }
            mutable.move(Direction.DOWN);
        }

        // Если не нашли, попробуем идти вверх от стартовой позиции
        mutable = startPos.mutableCopy();
        for (int i = 0; i < 32; i++) {
            BlockState state = world.getBlockState(mutable);
            if (state.isSolidBlock(world, mutable) && !state.isAir()) {
                return mutable.toImmutable();
            }
            mutable.move(Direction.UP);
        }

        return null;
    }

    private void generateMainColumn(StructureWorldAccess world, BlockPos startPos, BlockState block, BlockState skintCluster, int height, Random random) {
        BlockPos.Mutable mutable = startPos.mutableCopy();

        for (int i = 0; i < height; i++) {
            BlockState currentState = world.getBlockState(mutable);
            if (currentState.isAir() || canReplace(currentState)) {
                world.setBlockState(mutable, block, Block.NOTIFY_ALL);
            } else {
                // Если встретили препятствие, останавливаемся
                break;
            }
            mutable.move(Direction.UP);
        }

        if(random.nextBoolean()){
            // Генерируем случайный кристаллический кластер на вершине
            generateCrystalCluster(world, mutable, skintCluster);
        }
    }

    private void generate3x3Columns(StructureWorldAccess world, BlockPos center, BlockState block, BlockState skintCluster,
                                    int mainHeight, Random random) {

        // Проходим по всем позициям в сетке 3x3
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue; // Пропускаем центр (там уже главная колонна)

                BlockPos columnPos = center.add(x, 0, z);

                // Проверяем, что есть твердая поверхность под колонкой
                BlockState groundState = world.getBlockState(columnPos.down());
                if (!groundState.isSolidBlock(world, columnPos.down()) || groundState.isAir()) {
                    continue;
                }

                // Высота колонки: минимум 1, максимум (mainHeight - 2)
                int maxColumnHeight = Math.max(1, mainHeight - 2);
                int columnHeight = random.nextInt(maxColumnHeight) + 1;

                generateMainColumn(world, columnPos, block, skintCluster, columnHeight, random);
            }
        }
    }

    private void generateCrystalCluster(StructureWorldAccess world, BlockPos startPos, BlockState skintCluster){
        BlockPos.Mutable mutable = startPos.mutableCopy();
        BlockState currentState = world.getBlockState(mutable);

        if (currentState.isAir() || canReplace(currentState)) {
            world.setBlockState(mutable, skintCluster, Block.NOTIFY_ALL);
        }
    }

    // Проверка, можно ли заменить блок
    private boolean canReplace(BlockState state) {
        return state.isAir() ||
                state.isOf(Blocks.WATER) ||
                state.isOf(Blocks.CAVE_AIR) ||
                !state.isSolidBlock(null, null);
    }

    // Enum для типов сталагмитов
    public enum StalagmiteType {
        SMALL(5, 5),
        MEDIUM(5, 8),
        LARGE(5, 11);

        private final int minHeight;
        private final int maxHeight;

        StalagmiteType(int minHeight, int maxHeight) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }

        public int getMinHeight() {
            return minHeight;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public static StalagmiteType getRandom(Random random) {
            return values()[random.nextInt(values().length)];
        }
    }
}