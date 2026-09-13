package com.progressionplus.registry.block.custom;

import com.mojang.serialization.MapCodec;
import com.progressionplus.Progressionplus;
import com.progressionplus.registry.block.entity.ExperienceStoragePedestalEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class ExperienceStoragePedestal extends BlockWithEntity implements BlockEntityProvider {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape LOWER_SHAPE = BlockWithEntity.createCuboidShape(3, 0, 3, 13, 16, 13);
    private static final VoxelShape UPPER_SHAPE = BlockWithEntity.createCuboidShape(3, 0, 3, 13, 10, 13);
    public static final MapCodec<ExperienceStoragePedestal> CODEC = ExperienceStoragePedestal.createCodec(ExperienceStoragePedestal::new);

    public ExperienceStoragePedestal(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockPos upperPos = pos.up();

        if (world.isInBuildLimit(upperPos) &&
                world.getBlockState(upperPos).canReplace(ctx) &&
                ctx.canPlace()) {

            FluidState fluidState = world.getFluidState(pos);
            return this.getDefaultState()
                    .with(HALF, DoubleBlockHalf.LOWER)
                    .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockPos upperPos = pos.up();
            // Верхняя часть не будет иметь BlockEntity
            world.setBlockState(upperPos, this.getDefaultState()
                            .with(HALF, DoubleBlockHalf.UPPER)
                            .with(WATERLOGGED, world.getFluidState(upperPos).getFluid() == Fluids.WATER),
                    Block.NOTIFY_ALL);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            DoubleBlockHalf half = state.get(HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);

            if (otherState.isOf(this) && otherState.get(HALF) != half) {
                world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherPos, Block.getRawIdFromState(otherState));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockState lowerState = world.getBlockState(pos.down());
            return lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canPlaceAt(state, world, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Создаем BlockEntity только для нижней части
        return state.get(HALF) == DoubleBlockHalf.LOWER ?
                new ExperienceStoragePedestalEntity(pos, state) : null;
    }

    private ExperienceStoragePedestalEntity getPedestalEntity(World world, BlockPos pos, BlockState state) {
        // Всегда получаем BlockEntity из нижней части
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();

        if (world.getBlockEntity(lowerPos) instanceof ExperienceStoragePedestalEntity entity) {
            return entity;
        }
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ExperienceStoragePedestalEntity pedestalEntity = getPedestalEntity(world, pos, state);
        if (pedestalEntity == null) {
            return ActionResult.FAIL;
        }

        // Проверяем, зажат ли Shift
        if (player.isSneaking()) {
            // Shift + ПКМ по пустым рукам - передача опыта
            if (player.getMainHandStack().isEmpty()) {
                if (pedestalEntity.hasCrystal()) {
                    // Если есть опыт у игрока - передаем в блок
                    Progressionplus.LOGGER.info(player.totalExperience + "");
                    if (player.totalExperience > 0) {
                        if (pedestalEntity.tryStorePlayerExperience(player)) {
                            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                            Text text = Text.translatable("message.progression-plus.experience_stored");
                            player.sendMessage(Text.literal(text.getString() + pedestalEntity.getStoredExperience())
                                    .formatted(Formatting.GREEN), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                    // Если у игрока нет опыта, но в блоке есть - передаем игроку
                    else if (pedestalEntity.getStoredExperience() > 0) {
                        if (pedestalEntity.tryGiveExperienceToPlayer(player)) {
                            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                            Text text = Text.translatable("message.progression-plus.experience_retrieved");
                            player.sendMessage(Text.literal(text.getString())
                                    .formatted(Formatting.AQUA), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    Text text = Text.translatable("message.progression-plus.need_skint");
                    player.sendMessage(Text.literal(text.getString())
                            .formatted(Formatting.RED), true);
                }
            }
            // Shift + ПКМ с предметом - открытие GUI
            else {
                player.openHandledScreen(pedestalEntity);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Всегда работаем с нижней частью блока
        ExperienceStoragePedestalEntity pedestalBlockEntity = getPedestalEntity(world, pos, state);

        if(pedestalBlockEntity != null) {
            // Если зажат Shift - не обрабатываем здесь, пусть обрабатывает onUse
            if (player.isSneaking()) {
                onUse(state, world, pos, player ,hit);
                return ItemActionResult.SUCCESS;
            }

            if(pedestalBlockEntity.isEmpty() && !stack.isEmpty()) {
                // Проверяем, можно ли поместить предмет
                if (pedestalBlockEntity.canInsert(0, stack, null)) {
                    pedestalBlockEntity.setStack(0, stack.copyWithCount(1));
                    world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                    stack.decrement(1);

                    pedestalBlockEntity.markDirty();
                    updateBothParts(world, pos, state);
                } else {
                    if (!world.isClient) {
                        Text text = Text.translatable("message.progression-plus.only_skint");
                        player.sendMessage(Text.literal(text.getString())
                                .formatted(Formatting.RED), true);
                    }
                }
            } else if(stack.isEmpty() && !pedestalBlockEntity.isEmpty()) {
                ItemStack stackOnPedestal = pedestalBlockEntity.getStack(0);
                player.setStackInHand(Hand.MAIN_HAND, stackOnPedestal);
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                pedestalBlockEntity.clear();

                pedestalBlockEntity.markDirty();
                updateBothParts(world, pos, state);
            }
        }

        return ItemActionResult.SUCCESS;
    }

    // Вспомогательный метод для обновления обеих частей блока
    private void updateBothParts(World world, BlockPos pos, BlockState state) {
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockPos upperPos = lowerPos.up();

        world.updateListeners(lowerPos, world.getBlockState(lowerPos), world.getBlockState(lowerPos), Block.NOTIFY_ALL);
        world.updateListeners(upperPos, world.getBlockState(upperPos), world.getBlockState(upperPos), Block.NOTIFY_ALL);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}
