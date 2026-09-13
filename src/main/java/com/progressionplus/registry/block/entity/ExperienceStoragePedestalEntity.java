package com.progressionplus.registry.block.entity;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.ModItems;
import com.progressionplus.registry.block.ImplementedInventory;
import com.progressionplus.registry.block.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ExperienceStoragePedestalEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private float rotation = 0;
    private int storedExperience = 0; // Хранимый опыт

    public ExperienceStoragePedestalEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_PEDESTAL_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    // Геттер для получения количества опыта
    public int getStoredExperience() {
        return storedExperience;
    }

    // Добавить опыт
    public void addExperience(int experience) {
        this.storedExperience += experience;
    }

    // Проверить, есть ли нужный предмет в инвентаре
    public boolean hasCrystal() {
        ItemStack stack = getStack(0);
        if (stack.isEmpty()) return false;

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Identifier insertItemId = Registries.ITEM.getId(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
        return insertItemId.equals(itemId);
    }

    // Переопределяем методы для контроля вставки предметов
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable net.minecraft.util.math.Direction side) {
        if (slot != 0) return false;

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Identifier insertItemId = Registries.ITEM.getId(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
        return insertItemId.equals(itemId);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // Проверяем, можно ли вставить предмет
        if (slot == 0 && !stack.isEmpty()) {
            Identifier itemId = Registries.ITEM.getId(stack.getItem());
            Identifier insertItemId = Registries.ITEM.getId(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
            if (!insertItemId.equals(itemId)) {
                return; // Не разрешаем вставку
            }
        }
        inventory.set(slot, stack);
    }

    // Попытка передать опыт от игрока к блоку
    public boolean tryStorePlayerExperience(PlayerEntity player) {
        if (!hasCrystal()) {
            return false;
        }

        int playerExp = player.totalExperience;
        if (playerExp > 0) {
            addExperience(playerExp);
            player.totalExperience = 0;
            player.experienceLevel = 0;
            player.experienceProgress = 0.0f;
            return true;
        }
        return false;
    }

    // Попытка передать опыт от блока к игроку
    public boolean tryGiveExperienceToPlayer(PlayerEntity player) {
        if (storedExperience > 0) {
            player.addExperience(storedExperience);
            storedExperience = 0;
            return true;
        }
        return false;
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("StoredExperience", storedExperience);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        if (nbt.contains("StoredExperience")) {
            storedExperience = nbt.getInt("StoredExperience");
        } else {
            storedExperience = 0;
        }
    }
//
//    @Override
//    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
//        // Дропаем предметы только если есть что дропать
//        if (!this.isEmpty()) {
//            ItemScatterer.spawn(world, pos, this);
//        }
//        super.onBlockReplaced(pos, oldState);
//    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Experience storage pedestal");
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            // Отправляем обновление клиенту
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
