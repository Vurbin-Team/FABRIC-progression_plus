package com.progressionplus.block.renderer;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.block.custom.ExperienceStoragePedestal;
import com.progressionplus.registry.block.entity.ExperienceStoragePedestalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ExperienceStorageBlockEntityRenderer implements BlockEntityRenderer<ExperienceStoragePedestalEntity> {

    public ExperienceStorageBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(ExperienceStoragePedestalEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Получаем актуальный стак из инвентаря
        DefaultedList<ItemStack> inventory = entity.getItems();
        Progressionplus.LOGGER.info(inventory.toString());

        // Рендерим предмет с вращением
        matrices.push();
        matrices.translate(0.5, 1, 0.5);

        // Используем tickDelta для плавной анимации
        float rotation = entity.getRenderingRotation() + tickDelta * 0.5f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.scale(0.5f, 0.5f, 0.5f);

        // Получаем правильный уровень освещения
        int lightLevel = getLightLevel(entity.getWorld(), entity.getPos());

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                inventory.getFirst(), ModelTransformationMode.GROUND, lightLevel, overlay, matrices, vertexConsumers,
                entity.getWorld(), 0
        );

        matrices.pop();

        // Опционально: рендер дополнительных эффектов, если есть опыт
        if (entity.getStoredExperience() > 0) {
            renderExperienceEffect(entity);
        }
    }

    // Метод для рендера эффектов опыта (опционально)
    private void renderExperienceEffect(ExperienceStoragePedestalEntity entity) {

        MinecraftClient client = MinecraftClient.getInstance();
        Random random = entity.getWorld().getRandom(); // Используем мировой рандом, не клиентский

        // Получаем мировые координаты entity
        BlockPos pos = entity.getPos();

        // Ограничиваем частоту спавна частиц (не каждый тик)
        if (entity.getWorld().getTime() % 15 == 0) { // Каждые 10 тиков
            // Спавним меньше частиц за раз для оптимизации
            for (int i = 0; i < 2; i++) {
                // Случайные координаты вокруг центра блока
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.2;
                double y = pos.getY() + 1.0 + random.nextDouble() * 0.3;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.2;

                // Скорость движения частиц
                double velocityX = (random.nextDouble() - 0.5) * 0.08;
                double velocityY = 0.04 + random.nextDouble() * 0.06; // Медленнее движение вверх
                double velocityZ = (random.nextDouble() - 0.5) * 0.08;

                // Создаем частицу опыта
                client.particleManager.addParticle(
                        ParticleTypes.HAPPY_VILLAGER, // Зеленые частицы
                        x, y, z,
                        velocityX, velocityY, velocityZ
                );
            }
        }
    }


    // Метод для получения правильной BlockEntity с инвентарем
    private ExperienceStoragePedestalEntity getRenderEntity(ExperienceStoragePedestalEntity entity) {
        BlockState state = entity.getCachedState();
        World world = entity.getWorld();
        BlockPos pos = entity.getPos();

        if (world == null) return null;

        // Проверяем, является ли текущая позиция нижней частью
        if (state.get(ExperienceStoragePedestal.HALF) == DoubleBlockHalf.LOWER) {
            return entity; // Это уже нижняя часть
        } else {
            // Это верхняя часть, получаем нижнюю
            BlockPos lowerPos = pos.down();
            if (world.getBlockEntity(lowerPos) instanceof ExperienceStoragePedestalEntity lowerEntity) {
                return lowerEntity;
            }
        }

        return null;
    }

    private int getLightLevel(World world, BlockPos pos) {
        if (world == null) {
            return 15728880; // Максимальное освещение как fallback
        }

        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}