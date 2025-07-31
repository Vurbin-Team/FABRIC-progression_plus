package com.progressionplus.block.renderer;


import com.progressionplus.registry.block.custom.ExperienceStoragePedestal;
import com.progressionplus.registry.block.entity.ExperienceStoragePedestalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ExperienceStorageBlockEntityRenderer implements BlockEntityRenderer<ExperienceStoragePedestalEntity> {
    public ExperienceStorageBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(ExperienceStoragePedestalEntity entity, float tickProgress,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        // Получаем нижнюю часть блока для доступа к инвентарю
        ExperienceStoragePedestalEntity renderEntity = getRenderEntity(entity);

        if (renderEntity != null && !renderEntity.isEmpty()) {
            ItemStack stack = renderEntity.getStack(0);
            if (!stack.isEmpty()) {
                // Рендерим предмет
                matrices.push();

                // Позиционирование предмета
                matrices.translate(0.5, 1, 0.5); // Высота может отличаться
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderEntity.getRenderingRotation()));
                matrices.scale(0.5f, 0.5f, 0.5f);

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        stack, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers,
                        entity.getWorld(), 0
                );

                matrices.pop();
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
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}