package com.progressionplus.registry.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ChargedTitaniumSwordItem extends Item {
    // Настраиваемые параметры
    private static final double DAMAGE_RADIUS = 7.0; // Радиус урона
    private static final float DAMAGE_AMOUNT = 8.0f; // Количество урона
    private static final double EFFECT_SPEED = 0.3; // Скорость распространения эффектов
    private static final int EFFECT_DURATION = 20; // Длительность эффектов в тиках

    public ChargedTitaniumSwordItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 25;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && player.isOnGround()) {
            // Проверяем что игрок на земле
            performGroundSlam(world, player);
        }
        return stack;
    }

    private void performGroundSlam(World world, PlayerEntity player) {
        if (world.isClient) return;

        Vec3d playerPos = player.getPos();
        double playerY = player.getY();

        // Звук удара
        world.playSound(null, playerPos.x, playerPos.y, playerPos.z,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                1.0f, 0.8f);

        // Находим всех существ в радиусе
        Box damageBox = new Box(
                playerPos.x - DAMAGE_RADIUS, playerY - 1, playerPos.z - DAMAGE_RADIUS,
                playerPos.x + DAMAGE_RADIUS, playerY + 3, playerPos.z + DAMAGE_RADIUS
        );

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class, damageBox,
                entity -> entity != player && entity.squaredDistanceTo(playerPos) <= DAMAGE_RADIUS * DAMAGE_RADIUS
        );

        // Наносим урон всем найденным существам
        for (LivingEntity entity : entities) {
            DamageSource damageSource = world.getDamageSources().playerAttack(player);
            entity.damage((ServerWorld) world, damageSource, DAMAGE_AMOUNT);

            // Отбрасываем цель от игрока
            Vec3d knockback = entity.getPos().subtract(playerPos).normalize().multiply(1.5);
            entity.addVelocity(knockback.x, 0.5, knockback.z);
            entity.velocityModified = true;
        }

        // Запускаем визуальные эффекты
        startRippleEffect(world, player);
    }

    private void startRippleEffect(World world, PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d center = player.getPos();
        float playerYaw = player.getYaw();

        // Создаем эффект расходящихся кругов
        for (int tick = 0; tick < EFFECT_DURATION; tick++) {
            final int currentTick = tick;

            // Планируем выполнение через определенное количество тиков
            world.getServer().execute(() -> {
                if (currentTick * EFFECT_SPEED > DAMAGE_RADIUS) return;

                double currentRadius = currentTick * EFFECT_SPEED;
                createCircleParticles(serverWorld, center, currentRadius, playerYaw);

                // Дополнительные эффекты на земле
                if (currentTick % 3 == 0) {
                    createGroundCracks(serverWorld, center, currentRadius, playerYaw);
                }
            });
        }
    }

    private void createCircleParticles(ServerWorld world, Vec3d center, double radius, float yaw) {
        int particleCount = (int)(radius * 8); // Больше частиц для больших радиусов
        particleCount = Math.max(particleCount, 16);

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;

            // Учитываем поворот игрока
            double rotatedAngle = angle + Math.toRadians(yaw);

            double x = center.x + Math.cos(rotatedAngle) * radius;
            double z = center.z + Math.sin(rotatedAngle) * radius;
            double y = center.y + 0.1;


            // Частицы пыли
            world.spawnParticles(ParticleTypes.POOF,
                    x, y, z, 2, 0.2, 0.1, 0.2, 0.05);

            // Искры
            if (radius < DAMAGE_RADIUS * 0.7) {
                world.spawnParticles(ParticleTypes.CRIT,
                        x, y + 0.5, z, 1, 0.1, 0.3, 0.1, 0.1);
            }
        }
    }

    private void createGroundCracks(ServerWorld world, Vec3d center, double radius, float yaw) {
        // Создаем "трещины" в случайных направлениях
        for (int i = 0; i < 8; i++) {
            double angle = (2 * Math.PI * i) / 8 + Math.toRadians(yaw);

            for (double r = radius * 0.5; r <= radius; r += 0.3) {
                double x = center.x + Math.cos(angle) * r;
                double z = center.z + Math.sin(angle) * r;
                double y = center.y;

                // Находим уровень земли
                BlockPos groundPos = new BlockPos((int)x, (int)y, (int)z);
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos checkPos = groundPos.add(0, dy, 0);
                    if (!world.getBlockState(checkPos).isAir() &&
                            world.getBlockState(checkPos.up()).isAir()) {
                        y = checkPos.getY() + 1;
                        break;
                    }
                }

                // Частицы разрушения блоков
                world.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                        x, y, z, 3, 0.2, 0, 0.2, 0.1);

                // Дым от трещин
                world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                        x, y + 0.2, z, 1, 0.1, 0.1, 0.1, 0.02);
            }
        }

        // Дополнительный звуковой эффект
        if (radius > DAMAGE_RADIUS * 0.3 && radius < DAMAGE_RADIUS * 0.6) {
            world.playSound(null, center.x, center.y, center.z,
                    SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS,
                    0.5f, 0.8f + (float)(Math.random() * 0.4));
        }
    }
}